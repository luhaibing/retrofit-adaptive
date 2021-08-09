package com.mercer.adaptive.processor.model

import com.mercer.adaptive.processor.action.OnDerivativeNamed
import com.mercer.adaptive.processor.constant.API_SUFFIX
import com.mercer.adaptive.processor.constant.IMPL_SUFFIX
import com.mercer.adaptive.processor.constant.SERVICE_API_SUFFIX
import com.mercer.adaptive.processor.impl.OnDerivativeNamedImpl
import com.mercer.adaptive.processor.util.name
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
