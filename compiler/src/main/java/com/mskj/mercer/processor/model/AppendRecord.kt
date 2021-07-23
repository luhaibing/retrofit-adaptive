package com.mskj.mercer.processor.model

import com.mskj.mercer.core.emum.AppendKey
import com.squareup.kotlinpoet.TypeName

data class AppendRecord(
    val key: String,
    val appendKey: AppendKey,
    val provider: TypeName
)