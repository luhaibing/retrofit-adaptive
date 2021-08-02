package com.mercer.adaptive.core

/**
 * @author  mercer
 * @date    2021/4/30 - 11:22
 * @desc
 *      转化器
 */
interface Converter<Input, Output> {

    fun convert(primeval: Input): Output

}