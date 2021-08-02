package com.mercer.adaptive.core

interface AppendProvider {

    fun provider(url: String): String?

}