package com.mskj.mercer.app.network

import androidx.`annotation`.Keep
import com.mercer.adaptive.core.OnAdaptiveRetrofit
import com.mercer.adaptive.core.ParameterValueConverter
import com.mercer.adaptive.core.ParameterValueConverterImpl
import com.mercer.adaptive.core.Watermelon
import com.mskj.mercer.app.model.RepoResponse
import kotlinx.coroutines.*
import java.lang.NullPointerException
import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.collections.MutableMap
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Keep
class GitHubServiceImpl2 private constructor(
    private val url: String
) : OnAdaptiveRetrofit, GitHubService {

    private val api: GitHubServiceServiceApi2 by lazy {
        Watermelon().create(url)
    }

    private val converters: MutableMap<String, ParameterValueConverter> by lazy {
        hashMapOf()
    }

    private inline fun <reified T> converter(primeval: Any?): String? {
        val key = T::class.java.canonicalName!!
        var converter = converters[key]
        if (converter == null) {
            converter = ParameterValueConverterImpl()
            converters[key] = converter
        }
        return converter.convert(primeval)
    }

    private val scope:kotlinx.coroutines.CoroutineScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    override fun searchRepos1(page: Int, perPage: Int): Deferred<RepoResponse>
            /*= CompletableDeferred<RepoResponse>().apply {
                    scope.launch {
                        try {
                            complete(api.searchRepos1(page, perPage))
                        } catch (e: Exception) {
                            completeExceptionally(e)
                        }
                    }
                }*/ {
        val deferred = kotlinx.coroutines.CompletableDeferred<RepoResponse>()
        scope.launch {
            try {
                deferred.complete(api.searchRepos1(page, perPage))
            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            }
        }
        return deferred
    }

    override suspend fun searchRepos2(page: Int, perPage: Int): RepoResponse =
        api.searchRepos2(page, perPage)

    override fun searchRepos3(page: Int, perPage: Int): Flow<RepoResponse> = flow {
        emit(api.searchRepos3(page, perPage))
    }

    companion object {
        private val CACHE_MAP: MutableMap<String, GitHubService> by lazy {
            hashMapOf()
        }

        @JvmStatic
        operator fun invoke(url: String): GitHubService {
            if (url.isBlank()) throw NullPointerException("url can not be null or blank")
            return CACHE_MAP[url] ?: GitHubServiceImpl2(url).apply {
                CACHE_MAP[url] = this
            }
        }
        @JvmStatic
        operator fun invoke(): GitHubService = invoke("https://api.github.com/")
    }

}
