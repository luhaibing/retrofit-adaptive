package com.mskj.mercer.processor.model

import com.mskj.mercer.processor.constant.API_SUFFIX
import com.mskj.mercer.processor.constant.IMPL_SUFFIX
import com.mskj.mercer.processor.constant.SERVICE_API_SUFFIX
import com.mskj.mercer.processor.util.name
import com.squareup.kotlinpoet.TypeName
import javax.lang.model.element.TypeElement

data class TypeElementExtra(
    val typeElement: TypeElement,
    val pair: Pair<String, TypeName>,
    val converter: TypeName,
    val appends: List<AppendRecord>
) {

    fun packageName() = typeElement.enclosingElement.toString()

    fun typeName() = typeElement.name()

    fun implTypeName() = typeName() + IMPL_SUFFIX

    fun apiTypeName() = typeName().let {
        if (it.substring(it.length - API_SUFFIX.length).equals(API_SUFFIX, true)) {
            it.substring(0, it.length - API_SUFFIX.length) + SERVICE_API_SUFFIX
        } else {
            it + SERVICE_API_SUFFIX
        }
    }

}
