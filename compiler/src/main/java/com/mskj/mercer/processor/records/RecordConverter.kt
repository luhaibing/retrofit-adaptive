package com.mskj.mercer.processor.records

import com.mskj.mercer.core.Converter

interface RecordConverter<I, O> : Converter<I, O> {

    fun convert(): O

}