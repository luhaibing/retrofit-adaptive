package com.mskj.mercer.app.provider

import com.mercer.adaptive.core.DynamicUrlProvider

/**
 * @author      ：mercer
 * @date        ：2021-08-02  01:21
 * @description ：
 */
class UrlProvider : DynamicUrlProvider {
    override fun provider(): String = "www.baidu.com"
}