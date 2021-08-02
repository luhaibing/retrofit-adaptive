@file:Suppress("MemberVisibilityCanBePrivate")

package com.mercer.adaptive.core

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mercer.adaptive.core.factory.GsonConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class Watermelon private constructor() {

    private var retrofitClient: Retrofit? = null

    private var okHttpClient: OkHttpClient? = null

    private var gson: Gson? = null

    private object Holder {
        val INSTANCE = Watermelon()
    }

    companion object {
        operator fun invoke(): Watermelon {
            return Holder.INSTANCE
        }

        val maps: HashMap<String, Any> = hashMapOf()
    }

    fun okHttp(client: OkHttpClient): Watermelon {
        okHttpClient = client
        return this
    }
    
    fun retrofit(client: Retrofit): Watermelon {
        retrofitClient = client
        return this
    }

    fun gson(client: Gson): Watermelon {
        gson = client
        return this
    }

    fun getOkHttp(): OkHttpClient {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient.Builder()
                // .addInterceptor(initHttpLoggingInterceptor())
                .build()
        }
        return okHttpClient!!
    }

    fun getRetrofit(url: String): Retrofit {
        if (retrofitClient == null) {
            retrofitClient = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(getOkHttp())
                .baseUrl(url)
                .build()
        }
        return retrofitClient!!
    }

    fun getGson(): Gson {
        if (gson == null) {
            gson = Gson()
        }
        return gson!!
    }

//    fun initHttpLoggingInterceptor(block:((String)->Unit)= { println(it) }): HttpLoggingInterceptor {
//        //日志显示级别。
//        val level: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY;
//        // http拦截器
//        val loggingInterceptor = HttpLoggingInterceptor { message ->
//            block.invoke(message)
//        }
//        loggingInterceptor.setLevel(level)
//        return loggingInterceptor
//    }

    val cache: MutableMap<String, Any> = hashMapOf()

    inline fun <reified T> create(url: String): T {
        val key = url.trim()
        var api = cache[key] as? T
        if (api == null) {
            api = getRetrofit(key).create(T::class.java)
            maps[key] = api!!
        }
        return api
    }

}