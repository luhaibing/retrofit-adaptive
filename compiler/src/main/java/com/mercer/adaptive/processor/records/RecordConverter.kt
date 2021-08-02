package com.mercer.adaptive.processor.records

import com.mercer.adaptive.core.Converter

interface RecordConverter<I, O> : Converter<I, O> {

    fun convert(): O

}