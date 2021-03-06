package com.mskj.mercer.app.converter

import com.google.gson.Gson
import com.mercer.adaptive.core.ParameterValueConverter

class SimpleParameterValueConverterImpl:ParameterValueConverter {
    override fun convert(primeval: Any?): String? {
        return Gson().toJson(primeval)
    }
}