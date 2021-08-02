package com.mskj.mercer.app.provider

import com.mercer.adaptive.core.AppendProvider

class BusinessIdProvider : AppendProvider {

    override fun provider(url: String): String {
        return 72.toLong().toString()
    }

}