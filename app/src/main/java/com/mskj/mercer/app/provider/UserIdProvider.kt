package com.mskj.mercer.app.provider

import com.mercer.adaptive.core.AppendProvider

class UserIdProvider : AppendProvider {

    override fun provider(url: String): String {
        return 10011.toLong().toString()
    }

}