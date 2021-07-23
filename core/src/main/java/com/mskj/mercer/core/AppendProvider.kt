package com.mskj.mercer.core

interface AppendProvider {

    fun provider(url: String): String?

}