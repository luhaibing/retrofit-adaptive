package com.mercer.adaptive.processor.model

import com.mercer.adaptive.core.emum.AppendKey

/**
 * 请求体内容格式
 */
enum class ContentType(vararg val keys: AppendKey) {
    // 无格式
    NULL(AppendKey.HEADER, AppendKey.QUERY),

    // json
    JSON(AppendKey.HEADER, AppendKey.QUERY, AppendKey.JSON_KEY),

    // 多部分
    MULTIPART(AppendKey.HEADER, AppendKey.QUERY, AppendKey.PART),

    // 表单
    FORM(AppendKey.HEADER, AppendKey.QUERY, AppendKey.FIELD),
}