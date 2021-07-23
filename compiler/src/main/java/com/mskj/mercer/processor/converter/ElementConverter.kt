package com.mskj.mercer.processor.converter

import com.mskj.mercer.core.Converter
import com.squareup.kotlinpoet.TypeName

/**
 * 元素转换
 */
interface ElementConverter<Element> : Converter<Element, TypeName> {

    fun convert(primeval: TypeName): TypeName

    override fun convert(primeval: Element): TypeName

}