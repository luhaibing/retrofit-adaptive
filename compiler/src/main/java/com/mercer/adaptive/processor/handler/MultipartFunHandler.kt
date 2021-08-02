package com.mercer.adaptive.processor.handler

import com.mercer.adaptive.processor.action.BaseFunHandler
import com.mercer.adaptive.processor.model.ContentType
import com.mercer.adaptive.processor.records.MethodRecord
import com.squareup.kotlinpoet.TypeSpec
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement

class MultipartFunHandler(messenger: Messager) : BaseFunHandler(messenger) {

    override fun match(
        typeElement: TypeElement,
        record: MethodRecord
    ): Boolean {
        return record.contentType == ContentType.MULTIPART
    }

    override fun handle(
        typeElement: TypeElement,
        record: MethodRecord,
        implTypeSpec: TypeSpec.Builder,
        serviceTypeSpec: TypeSpec.Builder
    ) {
        serviceTypeSpec.addFunction(record.convertServiceFun().build())
        implTypeSpec.addFunction(record.convertImpl().build())
    }

}