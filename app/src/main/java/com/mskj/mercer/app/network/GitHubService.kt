package com.mskj.mercer.app.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mercer.adaptive.annotate.Adaptive
import com.mercer.adaptive.core.factory.GsonConverterFactory
import com.mskj.mercer.app.model.RepoResponse
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author      ：mercer
 * @date        ：2021-08-09  21:42
 * @description ：
 */
@Adaptive("https://api.github.com/")
interface GitHubService {

    @GET("search/repositories?sort=stars&q=Android")
    fun searchRepos1(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Deferred<RepoResponse>

    @GET("search/repositories?sort=stars&q=Android")
    suspend fun searchRepos2(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): RepoResponse

    @GET("search/repositories?sort=stars&q=Android")
    fun searchRepos3(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Flow<RepoResponse>

    companion object {

        private const val BASE_URL = "https://api.github.com/"

        fun create(): GitHubService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(GitHubService::class.java)
        }

    }

}