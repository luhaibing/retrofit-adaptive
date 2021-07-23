package com.mskj.mercer.annotate

// 注解
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class JsonKey(
    val value: String
)