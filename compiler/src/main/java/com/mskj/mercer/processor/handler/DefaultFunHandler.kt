package com.mskj.mercer.processor.handler

import com.mskj.mercer.processor.action.BaseFunHandler
import com.mskj.mercer.processor.model.ContentType
import com.mskj.mercer.processor.records.MethodRecord
import com.squareup.kotlinpoet.TypeSpec
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement

class DefaultFunHandler(messenger: Messager) : BaseFunHandler(messenger) {

    /**
     * 只有没有指定请求体的格式就属于默认情况
     */
    override fun match(
        typeElement: TypeElement,
        record: MethodRecord
    ): Boolean {
        return record.contentType == ContentType.NULL
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