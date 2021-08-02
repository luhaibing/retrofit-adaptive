package com.mskj.mercer.app.provider

import com.mercer.adaptive.core.AppendProvider

class OrderIdProvider : AppendProvider {

    override fun provider(url: String): String {
        return 10001.toLong().toString()
    }

}