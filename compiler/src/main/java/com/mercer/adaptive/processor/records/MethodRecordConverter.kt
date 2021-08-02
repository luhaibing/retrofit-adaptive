package com.mercer.adaptive.processor.records

import com.mercer.adaptive.core.Converter
import com.squareup.kotlinpoet.FunSpec

interface MethodRecordConverter : Converter<MethodRecord, FunSpec.Builder> {

    /**
     * 参数重排序
     */
    fun parameterReorder(): Map<Int, List<ParameterRecord>>

    /**
     * 间接接口类
     */
    fun convertServiceFun(): FunSpec.Builder

    /**
     * 原接口实现类
     */
    fun convertImpl(): FunSpec.Builder

}