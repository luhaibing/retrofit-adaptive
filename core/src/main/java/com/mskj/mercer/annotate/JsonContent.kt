package com.mskj.mercer.annotate

import com.mskj.mercer.core.ParameterValueConverterImpl
import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
annotation class JsonContent(
    val value: String = DEFAULT_VALUE
) {
    companion object {
        const val DEFAULT_VALUE = "Content-Type: application/json; charset=utf-8"
    }
}
