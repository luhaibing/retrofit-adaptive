package com.mskj.mercer.processor.handler

import com.mskj.mercer.annotate.JsonContent
import com.mskj.mercer.processor.action.BaseFunHandler
import com.mskj.mercer.processor.model.ContentType
import com.mskj.mercer.processor.model.NORMAL
import com.mskj.mercer.processor.records.AnnotationRecord
import com.mskj.mercer.processor.records.MethodRecord
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import retrofit2.http.Headers
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement

class JsonContentTypeFunHandler(messenger: Messager) : BaseFunHandler(messenger) {

    override fun match(
        typeElement: TypeElement, record: MethodRecord
    ): Boolean = record.contentType == ContentType.JSON

    override fun handle(
        typeElement: TypeElement,
        record: MethodRecord,
        implTypeSpec: TypeSpec.Builder,
        serviceTypeSpec: TypeSpec.Builder
    ) {
        // 为方法附加请求头
        (record.annotations.find {
            it.typeName == Headers::class.asTypeName()
        } ?: AnnotationRecord(
            Headers::class.asTypeName(),
            hashMapOf("value" to arrayListOf<String>()),
            NORMAL
        ).apply {
            record.annotations.add(this)
        }).also {
            val values = (it.members["value"] as Collection<*>)
                .map { v ->
                    val value = v.toString()
                    val s = if (value.startsWith("\"")) {
                        1
                    } else {
                        0
                    }
                    val e = if (value.endsWith("\"")) {
                        value.length - 1
                    } else {
                        value.length
                    }
                    val substring = value.substring(s, e)
                    substring
                }
                .toMutableSet()
            values.add(record.annotations.find { it.typeName == JsonContent::class.asTypeName() }
                ?.key() ?: JsonContent.DEFAULT_VALUE)
            it.members["value"] = values.toMutableList()
        }
        serviceTypeSpec.addFunction(record.convertServiceFun().build())

        val convertImplBuilder = record.convertImpl()
        implTypeSpec.addFunction(convertImplBuilder.build())

    }

}