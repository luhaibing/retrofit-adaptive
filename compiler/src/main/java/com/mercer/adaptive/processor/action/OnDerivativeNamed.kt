package com.mercer.adaptive.processor.action

import com.squareup.kotlinpoet.TypeName

/**
 * 相应衍生类命名
 */
interface OnDerivativeNamed {

    fun implType():String

    fun serviceType():String

}