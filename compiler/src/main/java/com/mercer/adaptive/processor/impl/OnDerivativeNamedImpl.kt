package com.mercer.adaptive.processor.impl

import com.mercer.adaptive.processor.action.OnDerivativeNamed
import com.mercer.adaptive.processor.constant.IMPL_SUFFIX
import com.mercer.adaptive.processor.constant.SERVICE_API_SUFFIX
import com.mercer.adaptive.processor.util.name
import javax.lang.model.element.TypeElement

class OnDerivativeNamedImpl(private val typeElement: TypeElement) : OnDerivativeNamed {

    //  const val IMPL_SUFFIX = "Impl"
    //  const val SERVICE_API_SUFFIX = "ServiceApi"
   private fun typeName() = typeElement.name()

    override fun implType() = typeName() + IMPL_SUFFIX

    override fun serviceType() = typeName() + SERVICE_API_SUFFIX/*.let {
        if (it.substring(it.length - API_SUFFIX.length).equals(API_SUFFIX, true)) {
            it.substring(0, it.length - API_SUFFIX.length) + SERVICE_API_SUFFIX
        } else {
            it + SERVICE_API_SUFFIX
        }
    }*/

}