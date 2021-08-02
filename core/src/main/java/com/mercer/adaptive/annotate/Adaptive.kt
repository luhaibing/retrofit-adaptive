package com.mercer.adaptive.annotate

// com.mercer.adaptive.processor

import com.mercer.adaptive.core.DynamicUrlProvider
import com.mercer.adaptive.core.EmptyDynamicUrlProvider
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
 *
 *  ps: 当fixed为空串和dynamic为EmptyDynamicUrlProvider时,
 *  不会生成无参的方法(伴生类的无参的 invoke 方法)
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class Adaptive(
    val fixed: String = "",
    val dynamic: KClass<out DynamicUrlProvider> = EmptyDynamicUrlProvider::class,
)






