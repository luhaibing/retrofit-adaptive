package com.mskj.mercer.app.network

import androidx.`annotation`.Keep
import com.mskj.mercer.app.model.RepoResponse
import kotlin.Int
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

@Keep
interface GitHubServiceServiceApi2 {
    @GET("search/repositories?sort=stars&q=Android")
    suspend fun searchRepos1(@Query("page") var0: Int, @Query("per_page") var1: Int)
            : RepoResponse

    @GET("search/repositories?sort=stars&q=Android")
    suspend fun searchRepos2(@Query("page") var0: Int, @Query("per_page") var1: Int)
            : RepoResponse

    @GET("search/repositories?sort=stars&q=Android")
    suspend fun searchRepos3(@Query("page") var0: Int, @Query("per_page") var1: Int)
            : RepoResponse
}
