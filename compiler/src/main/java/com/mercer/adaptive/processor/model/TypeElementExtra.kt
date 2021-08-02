package com.mercer.adaptive.processor.model

import com.mercer.adaptive.processor.action.OnDerivativeNamed
import com.mercer.adaptive.processor.impl.OnDerivativeNamedImpl
import com.squareup.kotlinpoet.TypeName
import javax.lang.model.element.TypeElement

data class TypeElementExtra(
    val typeElement: TypeElement,
    val pair: Pair<String, TypeName>,
    val converter: TypeName,
    val appends: List<AppendRecord>
) : OnDerivativeNamed by OnDerivativeNamedImpl(typeElement) {

    fun packageName() = typeElement.enclosingElement.toString()

}
