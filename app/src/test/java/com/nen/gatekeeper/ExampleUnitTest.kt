package com.nen.gatekeeper

import com.nen.gatekeeper.data.repo.PluginRepository
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testingPlugin() = runBlocking {
        val repository = PluginRepository(client = OkHttpClient())
        val url = "https://raw.githubusercontent.com/phisher98/cloudstream-extensions-phisher/builds/repo.json"
        val result = repository.fetchRepo(url)

        println("====================================")
        println("LIVE TEST RESULT: $result")
        result.onSuccess { repo ->
            println("REPO NAME: ${repo.name}")
            println("DESCRIPTION: ${repo.description}")
            println("PLUGINS LIST URL: ${repo.pluginLists}")
        }
        println("====================================")
    }
}
