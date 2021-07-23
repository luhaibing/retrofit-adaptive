@file:Suppress("unused")

package com.mskj.mercer.processor.records

import com.mskj.mercer.processor.constant.ADAPTIVE_PREFIX
import com.mskj.mercer.processor.constant.RETROFIT_PREFIX
import com.mskj.mercer.processor.converter.impl.TypeNameConverterImpl
import com.mskj.mercer.processor.converter.impl.VariableElementConverterImpl
import com.mskj.mercer.processor.model.AnnotationSource
import com.mskj.mercer.processor.model.ContentType
import com.mskj.mercer.processor.model.NORMAL
import com.mskj.mercer.processor.model.SUPPLEMENT
import com.mskj.mercer.annotate.JsonKey
import com.mskj.mercer.processor.util.*
import com.squareup.kotlinpoet.*
import org.jetbrains.annotations.Nullable
import retrofit2.http.*
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

data class ParameterRecord(
    val name: String,
    val typeName: TypeName,
    val nullable: Boolean,
    val annotations: MutableList<AnnotationRecord>,
    val position: Int,
    val source: AnnotationSource = NORMAL,
    val providerTypeName: TypeName? = null
) : RecordConverter<ParameterRecord, ParameterSpec.Builder> {

    override fun convert(primeval: ParameterRecord): ParameterSpec.Builder = primeval.convert()

    override fun convert(): ParameterSpec.Builder = ParameterSpec
        .builder(name, VariableElementConverterImpl.convert(typeName.copy(nullable)))
        .also { builder ->
            annotations
                .filter { annotationRecord ->
                    annotationRecord.typeName.toString().startsWith(RETROFIT_PREFIX)
                }
                .onEach {
                    builder.addAnnotation(it.convert().build())
                }
        }

}

/**
 * 只转换有注解的参数
 */
fun VariableElement.convert(): ParameterRecord {
    val annotationRecords = annotationMirrors.filter { annotationMirror ->
        // 具有 retrofit注解 和 自定义注解[JsonKey]
        annotationMirror.annotationType.toTypeName().toString().startsWith(RETROFIT_PREFIX) ||
                annotationMirror.annotationType.toTypeName().toString().startsWith(ADAPTIVE_PREFIX)
    }.map {
        it.convert()
    }
    return ParameterRecord(
        name(), TypeNameConverterImpl.convert(asType().toTypeName()),
        hasAnnotation(Nullable::class.java),
        arrayListOf(*annotationRecords.toTypedArray()),
        (enclosingElement as ExecutableElement).parameters.indexOf(this),
        NORMAL
    )
}


/**
 * 补齐请求体的内容的注解
 */
fun List<ParameterRecord>.makeUpContentAnnotation(contentType: ContentType): List<ParameterRecord> {
    filter {
        it.annotations.isEmpty()
    }.onEach { parameterRecord ->
        val isMap = parameterRecord.typeName.isMap()
        when (contentType) {
            ContentType.MULTIPART -> {
                if (isMap) {
                    AnnotationRecord(PartMap::class.asTypeName(), hashMapOf(), SUPPLEMENT)
                } else {
                    AnnotationRecord(
                        Part::class.asTypeName(),
                        hashMapOf("value" to parameterRecord.name),
                        SUPPLEMENT
                    )
                }
            }
            ContentType.FORM -> {
                if (isMap) {
                    AnnotationRecord(FieldMap::class.asTypeName(), hashMapOf(), SUPPLEMENT)
                } else {
                    AnnotationRecord(
                        Field::class.asTypeName(),
                        hashMapOf("value" to parameterRecord.name),
                        SUPPLEMENT
                    )
                }
            }
            ContentType.JSON -> {
                val founds = map { it ->
                    it to it.annotations.map { it.typeName }
                }.filter { (_, types) ->
                    types.isEmpty() || types.contains(JsonKey::class.asTypeName())
                }
                if (!parameterRecord.typeName.isPrimitive() && founds.size == 1) {
                    AnnotationRecord(Body::class.asTypeName(), hashMapOf(), SUPPLEMENT)
                } else {
                    AnnotationRecord(
                        JsonKey::class.asTypeName(),
                        hashMapOf("value" to parameterRecord.name),
                        SUPPLEMENT
                    )
                }
            }
            else -> {
                if (isMap) {
                    AnnotationRecord(QueryMap::class.asTypeName(), hashMapOf(), SUPPLEMENT)
                } else {
                    AnnotationRecord(
                        Query::class.asTypeName(),
                        hashMapOf("value" to parameterRecord.name),
                        SUPPLEMENT
                    )
                }
            }
        }.let {
            parameterRecord.annotations.add(it)
        }
    }
    return this
}

/**
 * 补齐 path 注解
 */
fun List<ParameterRecord>.makeUpPaths(path: String): List<ParameterRecord> {
    // 补全 path 注解
    val paths = path.split("/").filter {
        it.startsWith("{") && it.endsWith("}")
    }.map {
        it.substring("{".length, it.length - "}".length)
    }
    if (paths.isEmpty()) {
        return this
    }
    filter { parameterRecord ->
        paths.contains(parameterRecord.name) && (parameterRecord.annotations.isEmpty() || parameterRecord.annotations.all {
            it.typeName != Path::class.asTypeName()
        })
    }.onEach {
        it.annotations.add(
            AnnotationRecord(
                Path::class.asTypeName(),
                hashMapOf("value" to it.name),
                SUPPLEMENT,
            )
        )
    }
    return this
}