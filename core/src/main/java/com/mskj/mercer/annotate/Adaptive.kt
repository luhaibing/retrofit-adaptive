package com.mskj.mercer.annotate

import com.mskj.mercer.core.DynamicUrlProvider
import com.mskj.mercer.core.EmptyDynamicUrlProvider
import kotlin.reflect.KClass


/**
 * retrofit 框架适配器
 * 因为 项目的后台接口 不支持常用的表单数据
 * 只接受 请求头为 Content-Type: application/json; charset=utf-8 的类型
 * 即 接收 json 数据
 * 虽然可以使用 @Body 注解可以使用
 * 但是
 *  1.如果每个接口都新建Model类来传递,项目中就会增加很多类
 *  2.如果使用Map都传递,就无法很直观的看出接口需要哪些字段
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class Adaptive(
    val fixed: String = "",
    val dynamic: KClass<out DynamicUrlProvider> = EmptyDynamicUrlProvider::class,
)






