package com.mercer.adaptive.processor.handler

import com.mercer.adaptive.processor.action.BaseFunHandler
import com.mercer.adaptive.processor.records.MethodRecord
import com.squareup.kotlinpoet.TypeSpec
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement

class NotImplementedFunHandler(messenger: Messager) : BaseFunHandler(messenger) {

    override fun match(
        typeElement: TypeElement,
        record: MethodRecord
    ): Boolean {
        return true
    }

    override fun handle(
        typeElement: TypeElement,
        record: MethodRecord,
        implTypeSpec: TypeSpec.Builder,
        serviceTypeSpec: TypeSpec.Builder
    ) {
        error("can not handle $typeElement.${record.primeval} ")
    }

}