package com.mskj.mercer.processor.action

import com.mskj.mercer.processor.records.MethodRecord
import com.squareup.kotlinpoet.TypeSpec
import javax.lang.model.element.TypeElement

/**
 * 方法分析器
 * 处理
 */
interface FunHandler {

    /**
     * 匹配
     * @param record                方法元素生成的信息
     */
    fun match(
        typeElement: TypeElement ,
        record: MethodRecord
    ): Boolean

    /**
     * 分析和处理
     * @param typeElement           类型元素
     * @param record                方法元素生成的信息
     * @param implTypeSpec          原有接口类的实现类
     * @param serviceTypeSpec       生成的间接的接口类
     */
    fun handle(
        typeElement: TypeElement,
        record: MethodRecord,
        implTypeSpec: TypeSpec.Builder,
        serviceTypeSpec: TypeSpec.Builder
    )

}