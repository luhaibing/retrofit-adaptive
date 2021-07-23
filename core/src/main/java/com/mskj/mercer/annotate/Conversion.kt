package com.mskj.mercer.annotate

import com.mskj.mercer.core.ParameterValueConverterImpl
import kotlin.reflect.KClass

/**
 * 转换
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Conversion(
    val value: KClass<*> = ParameterValueConverterImpl::class
)
