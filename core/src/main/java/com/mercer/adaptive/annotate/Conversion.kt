package com.mercer.adaptive.annotate

import com.mercer.adaptive.core.ParameterValueConverterImpl
import kotlin.reflect.KClass

/**
 * 转换
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Conversion(
    val value: KClass<*> = ParameterValueConverterImpl::class
)
