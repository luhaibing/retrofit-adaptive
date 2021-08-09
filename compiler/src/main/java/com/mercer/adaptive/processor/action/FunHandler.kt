package com.mercer.adaptive.processor.action

import com.mercer.adaptive.processor.records.MethodRecord
import com.squareup.kotlinpoet.TypeSpec
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * 方法分析器
 */
abstract class FunHandler(private val messenger: Messager) {

    protected fun error(message: String) {
        messenger.printMessage(Diagnostic.Kind.ERROR, message)
    }

    /**
     * 匹配
     * @param record                方法元素生成的信息
     */
    abstract fun match(typeElement: TypeElement, record: MethodRecord): Boolean

    /**
     * 分析和处理
     * @param typeElement           类型元素
     * @param record                方法元素生成的信息
     * @param implTypeSpec          原有接口类的实现类
     * @param serviceTypeSpec       生成的间接的接口类
     */
    abstract fun handle(
        typeElement: TypeElement, record: MethodRecord,
        implTypeSpec: TypeSpec.Builder, serviceTypeSpec: TypeSpec.Builder
    )

}