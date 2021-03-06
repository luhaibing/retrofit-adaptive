package com.mercer.adaptive.processor.impl

import com.mercer.adaptive.processor.model.AppendRecord
import com.mercer.adaptive.processor.records.MethodRecord
import com.mercer.adaptive.processor.records.convert
import com.squareup.kotlinpoet.TypeName
import javax.lang.model.element.ExecutableElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class FunAnalyerImpl(
    private val types: Types,
    private val elements: Elements
) {

    /**
     * @param primeval      原始原色
     * @param typeAppends   类级别的附加参数
     */
    fun convert(
        primeval: ExecutableElement,
        typeAppends: List<AppendRecord>,
        providers: MutableSet<AppendRecord>,
        converters: MutableList<TypeName>
    ): MethodRecord {
        return primeval.convert(types,elements, typeAppends, providers, converters)
    }

}