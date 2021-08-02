package com.mercer.adaptive.annotate

import com.mercer.adaptive.core.emum.AppendKey
import kotlin.reflect.KClass

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Append(
    val key: String,
    val appendKey: AppendKey,
    val provider: KClass<*>
)