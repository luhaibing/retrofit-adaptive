package com.mskj.mercer.app.converter

import com.google.gson.Gson
import com.mskj.mercer.core.ParameterValueConverter


class Simple2ParameterValueConverterImpl : ParameterValueConverter {

    override fun convert(primeval: Any?): String? {
        return Gson().toJson(primeval)
    }

}