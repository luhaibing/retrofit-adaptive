package com.mskj.mercer.app.model


data class NetResponse<T>(
    val code: Int,
    val message: String,
    val result: T?,
    val success: Boolean,
)