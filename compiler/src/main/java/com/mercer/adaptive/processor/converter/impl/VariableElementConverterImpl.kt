package com.mercer.adaptive.processor.converter.impl

import com.mercer.adaptive.processor.converter.VariableElementConverter
import com.mercer.adaptive.processor.util.hasAnnotation
import com.mercer.adaptive.processor.util.toTypeName
import com.squareup.kotlinpoet.TypeName
import org.jetbrains.annotations.Nullable
import javax.lang.model.element.VariableElement

object VariableElementConverterImpl : VariableElementConverter {

    override fun convert(primeval: TypeName): TypeName {
        return TypeNameConverterImpl.convert(primeval)
    }

    override fun convert(primeval: VariableElement): TypeName {
        return convert(primeval.toTypeName())
            .copy(primeval.hasAnnotation(Nullable::class.java))
    }

}