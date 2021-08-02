package com.mercer.adaptive.processor.records

import com.mercer.adaptive.processor.model.AnnotationSource
import com.mercer.adaptive.processor.model.NORMAL
import com.mercer.adaptive.processor.util.name
import com.mercer.adaptive.processor.util.toTypeName
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import javax.lang.model.element.AnnotationMirror

data class AnnotationRecord(
    val typeName: TypeName,
    var members: MutableMap<String, Any>,
    val source: AnnotationSource,
    val primeval: AnnotationMirror? = null
) : RecordConverter<AnnotationRecord, AnnotationSpec.Builder> {

    fun key() = members["value"]?.toString()

    override fun convert(primeval: AnnotationRecord): AnnotationSpec.Builder = primeval.convert()

    override fun convert(): AnnotationSpec.Builder {
        return AnnotationSpec.builder(ClassName.bestGuess(typeName.toString()))
            .also { builder ->
                if (members.size == 1) {
                    when (val value = members.values.first()) {
                        is String -> {
                            builder.addMember("%S", value)
                        }
                        is Collection<*> -> {
                            val list = value.map {
                                it.toString().replace("\"", "")
                            }.map {
                                "\r\n\"$it\""
                            }.toString()
                            builder.addMember("%L", list.substring(1, list.length - 1))
                        }
                        else -> {
                            builder.addMember("%L", value)
                        }
                    }
                } else {
                    members.map { (key, value) ->
                        when (value) {
                            is String -> {
                                builder.addMember("%N=%S", key, value)
                            }
                            is Collection<*> -> {
                                val list = value.map {
                                    it.toString().replace("\"", "")
                                }.map {
                                    "\r\n\"$it\""
                                }.toString()
                                builder.addMember("%N=%L", key, list)
                            }
                            else -> {
                                builder.addMember("%N=%L", key, value)
                            }
                        }
                    }
                }
            }
    }

}

fun AnnotationMirror.convert(): AnnotationRecord {
    val members = elementValues
        .map { (key, value) ->
            key.name() to value.value
        }.toMap().toMutableMap()
    return AnnotationRecord(annotationType.toTypeName(), members, NORMAL, this)
}