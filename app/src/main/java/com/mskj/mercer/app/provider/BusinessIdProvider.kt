package com.mskj.mercer.app.provider

import com.mskj.mercer.core.AppendProvider

class BusinessIdProvider : AppendProvider {

    override fun provider(url: String): String {
        return 72.toLong().toString()
    }

}