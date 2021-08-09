package com.mercer.adaptive.processor

import com.mercer.adaptive.processor.action.FunHandler
import com.mercer.adaptive.processor.converter.impl.TypeNameConverterImpl
import com.mercer.adaptive.processor.model.AppendRecord
import com.mercer.adaptive.processor.model.TypeElementExtra
import com.mercer.adaptive.processor.util.collectConvert
import com.mercer.adaptive.processor.util.collectProviders
import com.mercer.adaptive.processor.util.toTypeName
import com.mercer.adaptive.annotate.Adaptive
import com.mercer.adaptive.core.ParameterValueConverterImpl
import com.mercer.adaptive.processor.constant.*
import com.mercer.adaptive.processor.impl.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

@Suppress("unused")
class AdaptiveProcessor : AbstractProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return hashSetOf(ADAPTIVE_FACTORY_CLASS_NAME)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    private val messenger: Messager by lazy { processingEnv.messager }

    private val filer: Filer by lazy { processingEnv.filer }

    private val elements: Elements by lazy { processingEnv.elementUtils }

    private val types: Types by lazy { processingEnv.typeUtils }

    private val handles: List<FunHandler> by lazy {
        arrayListOf<FunHandler>().apply {
            add(FormUrlEncodedFunHandler(messenger))
            add(MultipartFunHandler(messenger))
            add(JsonContentTypeFunHandler(messenger))
            add(DefaultFunHandler(messenger))
            add(NotImplementedFunHandler(messenger))
        }
    }

    private val providers: MutableSet<AppendRecord> by lazy { hashSetOf() }

    private val converters: MutableList<TypeName> by lazy { arrayListOf() }

    override fun process(
        mutableSet: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?
    ): Boolean {
        val adaptiveElements =
            roundEnvironment?.getElementsAnnotatedWith(Adaptive::class.java)
        val funAnalyzer = FunAnalyerImpl(types, elements)
        if (adaptiveElements.isNullOrEmpty()) {
            return true
        } else {
            providers.clear()
            converters.clear()
            try {
                listOf(*adaptiveElements.toTypedArray())
                    .asSequence()
                    .filterIsInstance<TypeElement>()
                    .filter {
                        it.kind == ElementKind.INTERFACE
                    }.map { typeElement ->
                        // 解析额外数据 方便后续的传递和使用
                        val adaptive = typeElement.getAnnotation(Adaptive::class.java)
                        val fixed: String = adaptive.fixed
                        val dynamic = try {
                            adaptive.dynamic
                            throw Exception()
                        } catch (e: MirroredTypeException) {
                            types.asElement(e.typeMirror).asType().toTypeName()
                        }
                        val appends = typeElement.collectProviders(types, elements)
                        providers.addAll(appends)

                        val converter: TypeName = typeElement.collectConvert(types, elements)
                            ?: ParameterValueConverterImpl::class.asClassName()
                        converters.add(converter)
                        TypeElementExtra(typeElement, fixed to dynamic, converter, appends) to
                                typeElement.enclosedElements
                    }.flatMap { (extra, elements) ->
                        // 展开类级元素 和 过滤掉非方法
                        elements.filter { it.kind == ElementKind.METHOD }.map {
                            extra to it as ExecutableElement
                        }
                    }.map { (extra, element) ->
                        // 方法分析/解析
                        extra to funAnalyzer.convert(element, extra.appends, providers, converters)
                    }.groupBy({ (extra, _) ->
                        extra
                    }, { (_, element) ->
                        element
                    }).map { (extra, records) ->

                        // 生成类的描述文件
                        val implTypeName = extra.implType()
                        val serviceApiTypeName = extra.serviceType()
                        val packageName = extra.packageName()
                        val typeElement = extra.typeElement
                        val pair = extra.pair
                        val serviceTypeBuilder = TypeSpec.interfaceBuilder(serviceApiTypeName)
                            .addAnnotation(CLASS_NAME_KEEP)

                        val returnType = typeElement.toTypeName()
                        val implTypeBuilder = TypeSpec.classBuilder(implTypeName)
                            .addSuperinterface(ANCHOR)
                            .addSuperinterface(returnType)
                            // 构造函数私有化
                            .primaryConstructor(
                                FunSpec.constructorBuilder()
                                    .addModifiers(KModifier.PRIVATE)
                                    .addParameter(ParameterSpec(NAME_URL, STRING))
                                    .build()
                            )
                            // 定义并初始化属性
                            .addProperty(
                                PropertySpec.builder(NAME_URL, STRING)
                                    .addModifiers(KModifier.PRIVATE)
                                    .initializer(NAME_URL)
                                    .build()
                            )
                            .addAnnotation(CLASS_NAME_KEEP)
                            .apply {
                                serviceApi(packageName, serviceApiTypeName)
                                companionTypeSpec(returnType, implTypeName, pair, packageName)
                                converter(converters)
                                provider(providers)
                            }

                        // 代码生成
                        records.onEach { record ->
                            handles.find {
                                it.match(typeElement, record)
                            }!!.handle(
                                typeElement,
                                record,
                                implTypeBuilder,
                                serviceTypeBuilder
                            )
                        }
                        extra to (implTypeBuilder.build() to serviceTypeBuilder.build())
                    }.onEach { (extra, pair) ->
                        val implTypeSpec = pair.first
                        val serviceTypeSpec = pair.second
                        val packageName = extra.packageName()

                        FileSpec.get(packageName, implTypeSpec)
                            .toBuilder()
                            .addImport("kotlinx.coroutines.flow", "flow")
                            .build()
                            .writeTo(filer)

                        FileSpec.get(packageName, serviceTypeSpec).writeTo(filer)
                    }
                    .toList()
            } catch (throwable: Throwable) {
                messenger.printMessage(Diagnostic.Kind.ERROR, throwable.message)
                throwable.printStackTrace()
            }
            return true
        }

    }

    private fun TypeSpec.Builder.companionTypeSpec(
        returnType: TypeName, implTypeName: String,
        pair: Pair<String, TypeName>, packageName: String
    ) {
        val cachePropertySpec = PropertySpec
            .builder(NAME_CACHE_MAP, MUTABLE_MAP.parameterizedBy(STRING, returnType))
            .delegate("lazy {\r\n%N()\r\n}", NAME_HASH_MAP_OF)
            .addModifiers(KModifier.PRIVATE)
            .build()

        val oneArgsFunSpec = FunSpec.builder(NAME_INVOKE)
            .addModifiers(KModifier.PUBLIC, KModifier.OPERATOR)
            .returns(returnType)
            .addAnnotation(JvmStatic::class.java)
            .addParameter(ParameterSpec(NAME_URL, STRING))
            .addStatement(
                "if (%N.isBlank()) throw %T(%S)",
                NAME_URL,
                NULL_POINTER_EXCEPTION,
                "$NAME_URL can not be null or blank"
            )
            .addStatement(
                "return %N[%N] ?: %T(%N).apply {\r\n%N[%N] = this\r\n}",
                NAME_CACHE_MAP, NAME_URL,
                ClassName(packageName, implTypeName), NAME_URL,
                NAME_CACHE_MAP, NAME_URL,
            )
            .build()

        val (fixed, dynamic) = pair
        val noArgsFunSpec = if (fixed.isBlank() && dynamic == CLASS_NAME_EMPTY_DYNAMIC_URL) {
            null
        } else {
            FunSpec.builder(NAME_INVOKE)
                .addModifiers(KModifier.PUBLIC, KModifier.OPERATOR)
                .returns(returnType)
                .addAnnotation(JvmStatic::class.java)
                .apply {
                    val fix = fixed.trim()
                    if (fix.isNotBlank()) {
                        addStatement("return %N(%S)", NAME_INVOKE, fix)
                    } else {
                        addStatement("return %N(%T().%N())", NAME_INVOKE, dynamic, NAME_PROVIDER)
                    }
                }
                .build()
        }

        addType(
            TypeSpec.companionObjectBuilder(null)
                .addModifiers(KModifier.PUBLIC)
                .addProperty(cachePropertySpec)
                .addFunction(oneArgsFunSpec)
                .apply {
                    noArgsFunSpec?.let { addFunction(it) }
                }
                .build()
        )
    }

    private fun TypeSpec.Builder.provider(providers: MutableSet<AppendRecord>) {
        val providersList = providers.map { it.provider }.distinct().toList()
        val size = providersList.size
        if (size == 0) {
            return
        }

        val providerPropertySpec = PropertySpec.builder(
            NAME_PROVIDERS, TypeNameConverterImpl.convert(
                MUTABLE_MAP_STRING_APPEND_PROVIDER
            )
        ).delegate("lazy {\r\n%N()\r\n}", NAME_HASH_MAP_OF)
            .addModifiers(KModifier.PRIVATE, KModifier.FINAL)
        addProperty(providerPropertySpec.build())

        val t = TypeVariableName("T").copy(reified = true)
        val providerFunSpecBuilder = FunSpec.builder(NAME_PROVIDER)
            .returns(STRING_NULLABLE)
            .addParameter(ParameterSpec(NAME_PATH, TypeNameConverterImpl.convert(STRING)))
            .addTypeVariable(t)
            .addModifiers(KModifier.INLINE, KModifier.FINAL, KModifier.PRIVATE)
            .addStatement("val %N = %T::class.java.canonicalName!!", NAME_KEY, t)
            .addStatement("var %N = %N[%N]", NAME_PROVIDER, NAME_PROVIDERS, NAME_KEY)

        val codeBlock = CodeBlock.builder()
            .add("if (%N == null) {", NAME_PROVIDER)
            .apply {
                if (size == 1) {
                    addStatement("%N = %T()", NAME_PROVIDER, providersList.first())
                    return@apply
                }
                for (i in 0 until size) {
                    val provider = providersList[i]
                    when (i) {
                        0 -> {
                            add(
                                "%N = if (%N == %T::class.java.canonicalName) {\r\n",
                                NAME_PROVIDER, NAME_KEY, provider
                            )
                            addStatement("%T()", provider)
                        }
                        size - 1 -> {
                            add("} else {\r\n")
                            addStatement("%T()", provider)
                        }
                        else -> {
                            add(
                                "} else if (%N == %T::class.java.canonicalName) {\r\n",
                                NAME_KEY,
                                provider
                            )
                            addStatement("%T()", provider)
                        }
                    }
                }
                addStatement("}")
            }
            .addStatement("%N[%N] = %N", NAME_PROVIDERS, NAME_KEY, NAME_PROVIDER)
            .addStatement("}")

        providerFunSpecBuilder.addCode(codeBlock.build())
        providerFunSpecBuilder.addStatement(
            "return %N.%N(%N)",
            NAME_PROVIDER,
            NAME_PROVIDER,
            NAME_PATH
        )
        addFunction(providerFunSpecBuilder.build())
    }

    private fun TypeSpec.Builder.converter(converters: MutableList<TypeName>) {
        val converterList = converters.distinct().toList()
        val size = converterList.size
        if (size == 0) {
            return
        }

        val converterPropertySpec = PropertySpec.builder(
            NAME_CONVERTERS, TypeNameConverterImpl
                .convert(MUTABLE_MAP_STRING_PARAMETER_VALUE_CONVERTER)
        ).delegate("lazy {\r\n%N()\r\n}", NAME_HASH_MAP_OF)
            .addModifiers(KModifier.PRIVATE, KModifier.FINAL)
        addProperty(converterPropertySpec.build())

        val t = TypeVariableName("T").copy(reified = true)
        val converterFunSpecBuilder = FunSpec.builder(NAME_CONVERTER).returns(STRING_NULLABLE)
            .addParameter(
                ParameterSpec(
                    NAME_PRIMEVAL,
                    TypeNameConverterImpl.convert(ANY_NULLABLE)
                )
            )
            .addTypeVariable(t)
            .addModifiers(KModifier.INLINE, KModifier.FINAL, KModifier.PRIVATE)
            .addStatement("val %N = %T::class.java.canonicalName!!", NAME_KEY, t)
            .addStatement("var %N = %N[%N]", NAME_CONVERTER, NAME_CONVERTERS, NAME_KEY)

        val codeBlock = CodeBlock.builder()
            .add("if (%N == null) {", NAME_CONVERTER)
            .apply {
                if (size == 1) {
                    addStatement("%N = %T()", NAME_CONVERTER, converterList.first())
                    return@apply
                }
                for (i in 0 until size) {
                    val converter = converterList[i]
                    when (i) {
                        0 -> {
                            add(
                                "%N = if (%N == %T::class.java.canonicalName) {\r\n",
                                NAME_CONVERTER, NAME_KEY, converter
                            )
                            addStatement("%T()", converter)
                        }
                        size - 1 -> {
                            add("} else {\r\n")
                            addStatement("%T()", converter)
                        }
                        else -> {
                            add(
                                "} else if (%N == %T::class.java.canonicalName) {\r\n",
                                NAME_KEY,
                                converter
                            )
                            addStatement("%T()", converter)
                        }
                    }
                }
                addStatement("}")
            }
            .addStatement("%N[%N] = %N", NAME_CONVERTERS, NAME_KEY, NAME_CONVERTER)
            .addStatement("}")

        converterFunSpecBuilder.addCode(codeBlock.build())
        converterFunSpecBuilder.addStatement(
            "return %N.%N(%N)",
            NAME_CONVERTER,
            NAME_CONVERT,
            NAME_PRIMEVAL
        )
        addFunction(converterFunSpecBuilder.build())
    }

    private fun TypeSpec.Builder.serviceApi(packageName: String, serviceApiTypeName: String) {
        val serviceApiClassName = ClassName(packageName, serviceApiTypeName)
        val serviceApiPropertySpec = PropertySpec.builder(
            NAME_SERVICE_API, serviceApiClassName
        ).delegate(
            "lazy {\r\n%T().create<%T>(%N)\r\n}",
            CLASS_NAME_RETROFIT_MANAGER, serviceApiClassName, NAME_URL
        ).addModifiers(KModifier.PRIVATE)
        addProperty(serviceApiPropertySpec.build())
    }

}