package com.nen.gatekeeper

import com.nen.gatekeeper.data.repo.PluginRepository
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Test
import java.io.File

class ExampleUnitTest {
    @Test
    fun testingPlugin() = runBlocking {
        val repository = PluginRepository(client = OkHttpClient())
        
        // 1. Repo info fetch karo
        val repoUrl = "https://raw.githubusercontent.com/phisher98/cloudstream-extensions-phisher/builds/repo.json"
        val repo = repository.fetchRepo(repoUrl).getOrThrow()
        println("1. REPO NAME: ${repo.name}")

        // 2. Us repo ke saare plugins fetch karo
        val pluginsListUrl = repo.pluginLists.first()
        val plugins = repository.fetchPlugins(pluginsListUrl).getOrThrow()
        println("2. TOTAL PLUGINS FOUND: ${plugins.size}")

        // 3. Pehle plugin ko download karo!
        val targetPlugin = plugins.first()
        println("3. DOWNLOADING: ${targetPlugin.name} (${targetPlugin.url})")
        
        val installed = repository.downloadPlugin(targetPlugin).getOrThrow()
        
        // 4. Check karo ki kya disk par sach me file bani aur kitne bytes ki hai!
        val diskFile = File(installed.localFilePath)
        println("====================================")
        println("SUCCESSFULLY INSTALLED: ${installed.name}")
        println("FILE PATH: ${diskFile.absolutePath}")
        println("FILE EXISTS ON DISK: ${diskFile.exists()}")
        println("REAL FILE SIZE: ${diskFile.length()} BYTES")
        println("====================================")
    }
}
