package com.mercer.adaptive.core

import com.google.gson.Gson

/**
 * @author  mercer
 * @date    2021/4/30 - 11:24
 * @desc
 *      参数转换器
 */
class ParameterValueConverterImpl : ParameterValueConverter {

    private val gson: Gson by lazy { Gson() }

    override fun convert(primeval: Any?): String? {
        if (primeval is String) {
            return primeval
        }
        return gson.toJson(primeval)
    }

}