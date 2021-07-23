@file:Suppress("MemberVisibilityCanBePrivate")

package com.mskj.mercer.processor.records

import com.mskj.mercer.processor.converter.impl.VariableElementConverterImpl
import com.mskj.mercer.processor.model.APPEND
import com.mskj.mercer.processor.model.AppendRecord
import com.mskj.mercer.processor.model.ContentType
import com.mskj.mercer.annotate.JsonContent
import com.mskj.mercer.processor.constant.*
import com.mskj.mercer.processor.util.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import retrofit2.http.*
import javax.lang.model.element.ExecutableElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.naming.OperationNotSupportedException

data class MethodRecord(
    val path: String,
    val name: String,
    val contentType: ContentType,
    val returnType: ParameterizedTypeName,
    val annotations: MutableList<AnnotationRecord>,
    val parameters: MutableList<ParameterRecord>,
    val typeAppends: List<AppendRecord>,
    val appends: List<AppendRecord>,
    val typeConverter: TypeName,
    val converter: TypeName?,
    val primeval: ExecutableElement
) : MethodRecordConverter {

    fun returnRawType() = returnType.rawType

    fun returnArgumentsType() = returnType.typeArguments[0]

    override fun convert(primeval: MethodRecord): FunSpec.Builder = primeval.convertImpl()

    override fun parameterReorder(): Map<Int, List<ParameterRecord>> {
        val values = hashMapOf<Int, List<ParameterRecord>>()

        values[CLASS_NAME_RETROFIT_FIRST_ECHELON_INDEX] = echelon(CLASS_NAME_RETROFIT_FIRST_ECHELON)

        values[CLASS_NAME_RETROFIT_SECOND_ECHELON_INDEX] =
            echelon(CLASS_NAME_RETROFIT_SECOND_ECHELON)

        values[CLASS_NAME_RETROFIT_THIRD_ECHELON_INDEX] = echelon(CLASS_NAME_RETROFIT_THIRD_ECHELON)

        values[CLASS_NAME_RETROFIT_FOURTH_ECHELON_INDEX] =
            echelon(CLASS_NAME_RETROFIT_FOURTH_ECHELON)

        values[CLASS_NAME_RETROFIT_FIFTH_ECHELON_INDEX] = echelon(CLASS_NAME_RETROFIT_FIFTH_ECHELON)

        values[CLASS_NAME_RETROFIT_SIXTH_ECHELON_INDEX] = echelon(CLASS_NAME_RETROFIT_SIXTH_ECHELON)

        return values

    }

    /**
     * 梯队分层
     */
    private fun echelon(contents: ArrayList<ClassName>) = parameters.filter { parameterRecord ->
        parameterRecord.annotations.map { annotationRecord ->
            annotationRecord.typeName
        }.any {
            contents.contains(it)
        }
    }.map { record ->
        ParameterRecord(
            record.name,
            record.typeName,
            record.nullable,
            record.annotations.filter { annotationRecord ->
                contents.contains(annotationRecord.typeName)
            }.toMutableList(),
            record.position,
            record.source,
            record.providerTypeName
        )
    }

    override fun convertServiceFun(): FunSpec.Builder {
        val builder = FunSpec.builder(name)
            .returns(DEFERRED_CLASS_NAME.parameterizedBy(returnArgumentsType()))
            .addModifiers(KModifier.ABSTRACT)

        val parameterReorder = parameterReorder()

        // 前面梯队有序命名
        parameterReorder.values.asSequence().take(parameterReorder.size - 1).flatten()//.distinct()
            .mapIndexed { index, parameterRecord ->
                val convert = parameterRecord.convert()
                convert.build().toBuilder(
                    NAME_VAR + index,
                    VariableElementConverterImpl.convert(
                        parameterRecord.typeName.copy(
                            parameterRecord.nullable
                        )
                    )
                )
            }.onEach {
                builder.addParameter(it.build())
            }.toList()

        // 末尾梯队(jsonKey)合并后有序命名
        if (parameterReorder.values.last().isNotEmpty()) {
            builder.addParameter(
                ParameterSpec.builder(
                    NAME_VAR + builder.parameters.size, STRING_NULLABLE
                ).addAnnotation(Body::class.asTypeName()).build()
            )
        }

        annotations
            .filter { annotationRecord ->
                annotationRecord.typeName.toString().startsWith(RETROFIT_PREFIX)
            }
            .onEach {
                builder.addAnnotation(it.convert().build())
            }
        return builder
    }

    override fun convertImpl(): FunSpec.Builder {
        val builder = FunSpec.builder(name)
            .addModifiers(KModifier.FINAL, KModifier.PUBLIC, KModifier.OVERRIDE).apply {
                if (returnRawType() == CONTINUATION_CLASS_NAME) {
                    addModifiers(KModifier.SUSPEND)
                    returns(returnArgumentsType())
                } else {
                    returns(returnType)
                }
            }

        val parameterReorder = parameterReorder()
        parameters
            .filter {
                it.position >= 0
            }
            .onEach {
                builder.addParameter(it.convert()
                    .apply {
                        annotations.clear()
                    }
                    .build())
            }
        val methodStringBuilder = StringBuilder()
        methodStringBuilder.append("$NAME_SERVICE_API.${name}(")
            .append("\r\n")

        val flatten = parameterReorder.values.take(parameterReorder.size - 1).flatten()

        parameterReorder.values.flatten()
            .filter {
                // it.position < 0
                it.source == APPEND
            }
            .distinctBy { it.name }
            .onEach {
                builder.addStatement(
                    "val %N = %N<%T>(%S)", it.name, NAME_PROVIDER, it.providerTypeName!!, path
                )
            }

        flatten.map {
            methodStringBuilder.append(it.name).append(",")
        }

        val jsonKeys = parameterReorder.values.last()
        if (jsonKeys.isNotEmpty()) {
            // 检查命名是否为占用
            val nameMap = avoidNameDuplication(NAME_MAP, parameters.map { it.name })
            builder.addStatement("val %N = hashMapOf<%T, %T>()", nameMap, STRING, ANY_NULLABLE)
            jsonKeys
                .onEach {
                    val key = it.annotations.first().key()!!
                    builder.addStatement("%N.put(%S, %N)", nameMap, key, it.name)
                }

            methodStringBuilder
                .append("$NAME_CONVERTER<%T>($nameMap)")
        }

        methodStringBuilder.append("\r\n")
            .append(")")

        when (returnRawType()) {
            DEFERRED_CLASS_NAME -> {
                // Deferred
                builder.addStatement("return $methodStringBuilder", converter ?: typeConverter)
            }
            FLOW_CLASS_NAME -> {
                // Flow
                builder.addCode("return flow {\r\n")
                    .addCode("emit(\r\n")
                    .addCode(methodStringBuilder.toString(), converter ?: typeConverter)
                    .addCode(".await()")
                    .addCode(")")
                    .addCode("}")
            }
            else -> {
                // 挂起函数
                builder.addCode("return ")
                    .addCode(methodStringBuilder.toString(), converter ?: typeConverter)
                    .addCode(".await()")
            }
        }
        return builder
    }

}

fun ExecutableElement.convert(
    types: Types,
    elements: Elements,
    typeAppends: List<AppendRecord>,
    providers: MutableSet<AppendRecord>,
    converters: MutableList<TypeName>
): MethodRecord {
    val path = url()
    val contentType = contentType()

    val appends = collectProviders(types, elements)
    val parameters = parameterRecords(contentType, path)

    val appendList = arrayListOf<AppendRecord>()
    providers.addAll(appendList)

    appends.onEach { append ->
        if (!appendList.any { it.key == append.key && it.appendKey == append.appendKey }) {
            appendList.add(append)
        }
    }
    typeAppends.onEach { append ->
        if (!appendList.any { it.key == append.key && it.appendKey == append.appendKey }) {
            appendList.add(append)
        }
    }

    for (append in appendList) {
        // 不符合当前内容格式
        if (!contentType.keys.contains(append.appendKey)) {
            continue
        }
        // 已包含该字段参数
        if (parameters.any { parameterRecord ->
                parameterRecord.annotations.any { annotationRecord ->
                    annotationRecord.key() == append.key && annotationRecord.typeName == append.appendKey.value.toClassName()
                }
            }) {
            continue
        }
        parameters.add(
            ParameterRecord(
                append.key,
                STRING,
                true,
                arrayListOf(
                    AnnotationRecord(
                        append.appendKey.value.toClassName(),
                        hashMapOf("value" to append.key),
                        APPEND,
                    )
                ), -1,
                APPEND,
                append.provider
            )
        )
    }

    val converter = collectConvert(types, elements)?.let {
        converters.add(it)
        it
    }

    return MethodRecord(
        path,
        name(),
        contentType,
        returnTypeName(),
        annotationRecords(),
        parameters,
        typeAppends,
        appends,
        converters.first(),
        converter,
        this
    )
}

private fun ExecutableElement.parameterRecords(
    contentType: ContentType,
    path: String
): MutableList<ParameterRecord> {
    // 有效参数
    val realParameters = parameters.take(
        if (returnTypeName().rawType == CONTINUATION_CLASS_NAME) {
            parameters.size - 1
        } else {
            parameters.size - 0
        }
    )
    val parameterRecords = realParameters
        .map {
            it.convert()
        }
    parameterRecords
        // 补齐 path
        .makeUpPaths(path)
        // 补齐 内容注解
        .makeUpContentAnnotation(contentType)
    return parameterRecords.toMutableList()
}

private fun ExecutableElement.annotationRecords(): MutableList<AnnotationRecord> {
    return annotationMirrors.filter { annotationMirror ->
        // 具有 retrofit注解 和 自定义注解[JsonKey]
        annotationMirror.annotationType.toTypeName().toString().startsWith(RETROFIT_PREFIX) ||
                annotationMirror.annotationType.toTypeName().toString().startsWith(ADAPTIVE_PREFIX)
    }.map {
        it.convert()
    }.toMutableList()
}

/**
 * 返回值类型
 */
private fun ExecutableElement.returnTypeName(): ParameterizedTypeName {
    val returnType = VariableElementConverterImpl.convert(returnType.toTypeName())
    if (returnType is ParameterizedTypeName && returnType.rawType == DEFERRED_CLASS_NAME) {
        // Deferred
        return returnType
    }
    if (returnType is ParameterizedTypeName && returnType.rawType == FLOW_CLASS_NAME) {
        // Flow
        return returnType
    }
    if (parameters.isEmpty()) {
        throw OperationNotSupportedException(
            "can parse returnType : ${enclosingElement.toTypeName()}.${name()}"
        )
    }
    val parameterType = VariableElementConverterImpl.convert(parameters.last())
    if (returnType == ANY && parameterType is ParameterizedTypeName &&
        parameterType.rawType == CONTINUATION_CLASS_NAME
    ) {
        // Continuation 即为 suspend 函数
        return CONTINUATION_CLASS_NAME.parameterizedBy(parameterType.typeArguments)
    }
    throw OperationNotSupportedException(
        "can parseReturnType : ${enclosingElement.toTypeName()}.${name()}"
    )
}

/**
 * url
 */
private fun ExecutableElement.url() =
    getAnnotation(POST::class.java)?.value ?: getAnnotation(DELETE::class.java)?.value
    ?: getAnnotation(PUT::class.java)?.value ?: getAnnotation(GET::class.java)?.value
    ?: throw OperationNotSupportedException("can parse url : ${enclosingElement.toTypeName()}.${name()}")

/**
 * 请求体类型
 */
private fun ExecutableElement.contentType(): ContentType =
    if (
        hasAnnotation(FormUrlEncoded::class) &&
        !hasAnnotation(Multipart::class) &&
        !hasAnnotation(JsonContent::class)
    ) {
        ContentType.FORM
    } else if (
        !hasAnnotation(FormUrlEncoded::class) &&
        hasAnnotation(Multipart::class) &&
        !hasAnnotation(JsonContent::class)
    ) {
        ContentType.MULTIPART
    } else if (
        !hasAnnotation(FormUrlEncoded::class) &&
        !hasAnnotation(Multipart::class) &&
        hasAnnotation(JsonContent::class)
    ) {
        ContentType.JSON
    } else {
        ContentType.NULL
    }