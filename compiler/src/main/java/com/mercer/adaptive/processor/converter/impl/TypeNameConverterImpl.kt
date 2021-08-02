package com.mercer.adaptive.processor.converter.impl

import com.mercer.adaptive.core.Converter
import com.mercer.adaptive.processor.constant.COLLECTION_SHINE_UPON
import com.mercer.adaptive.processor.constant.PRIMEVAL_SHINE_UPON
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.WildcardTypeName

/**
 * 类型名转化器
 */
object TypeNameConverterImpl : Converter<TypeName, TypeName> {

    override fun convert(primeval: TypeName): TypeName {
        val nullable = primeval.isNullable
        val input = primeval.copy(nullable = false)
        val result = PRIMEVAL_SHINE_UPON[input]
        val output = when {
            result != null -> {
                result
            }
            input is ParameterizedTypeName -> {
                var rawType = input.rawType
                val typeArguments = input.typeArguments
                rawType = COLLECTION_SHINE_UPON[rawType] ?: rawType
                rawType.parameterizedBy(typeArguments.map {
                    convert(it)
                })
            }
            input is WildcardTypeName -> {
                val effectiveValueTypeName = if (input.inTypes.isEmpty()) {
                    input.outTypes[0]
                } else {
                    input.inTypes[0]
                }
                if (effectiveValueTypeName is ParameterizedTypeName) {
                    var rawType = effectiveValueTypeName.rawType
                    val typeArguments = effectiveValueTypeName.typeArguments
                    rawType = COLLECTION_SHINE_UPON[rawType] ?: rawType
                    rawType.parameterizedBy(typeArguments.map {
                        convert(it)
                    })
                } else {
                    convert(effectiveValueTypeName)
                }
            }
            else -> {
                input.copy(nullable = nullable)
            }
        }
        return output.copy(nullable = nullable)
    }

}