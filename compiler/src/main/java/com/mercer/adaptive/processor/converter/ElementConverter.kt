package com.mercer.adaptive.processor.converter

import com.mercer.adaptive.core.Converter
import com.squareup.kotlinpoet.TypeName

/**
 * 元素转换
 */
interface ElementConverter<Element> : Converter<Element, TypeName> {

    fun convert(primeval: TypeName): TypeName

    override fun convert(primeval: Element): TypeName

}