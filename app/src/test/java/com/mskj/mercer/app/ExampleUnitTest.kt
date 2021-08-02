package com.mercer.adaptive.app

import com.mskj.mercer.app.network.GitHubService
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import com.mskj.mercer.app.network.GitHubServiceImpl
import com.mskj.mercer.app.network.GitHubServiceImpl2

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun addition_isCorrect2() = runBlocking {
        val api: GitHubService = GitHubServiceImpl()

        val searchRepos1 = api.searchRepos1(1, 20).await()
        val searchRepos2 = api.searchRepos2(1, 20)
        val searchRepos3 = api.searchRepos3(1, 20).singleOrNull()

        println(searchRepos1.items)
        println(searchRepos2.items)
        println(searchRepos3?.items)
    }


}