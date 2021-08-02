package com.mercer.adaptive.processor.model

import com.mercer.adaptive.core.emum.AppendKey
import com.squareup.kotlinpoet.TypeName

data class AppendRecord(
    val key: String,
    val appendKey: AppendKey,
    val provider: TypeName
)