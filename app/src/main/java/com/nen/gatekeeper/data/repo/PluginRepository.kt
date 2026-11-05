package com.nen.gatekeeper.data.repo

import android.content.Context
import com.nen.gatekeeper.data.local.AppPreferences
import com.nen.gatekeeper.data.model.InstalledPlugin
import com.nen.gatekeeper.data.model.PluginMetadata
import com.nen.gatekeeper.data.model.RepoMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PluginRepository @Inject constructor(
    private val client: OkHttpClient,
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferences
) {
    val json = Json { ignoreUnknownKeys = true }
    private suspend inline fun <reified T> fetchJson(url: String): Result<T> =
        withContext(Dispatchers.IO) {
            try {
                val cleanUrl = if (url.startsWith("cloudstreamrepo://")) {
                    "https://" + url.removePrefix("cloudstreamrepo://").removePrefix("https://")
                } else url
                val request = Request.Builder().url(cleanUrl).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@withContext Result.failure(Exception("HTTP Error: ${response.code}"))
                    }
                    val jsonString = response.body?.string()
                        ?: return@withContext Result.failure(Exception("Response body is empty"))

                    val data = json.decodeFromString<T>(jsonString)
                    Result.success(data)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun fetchRepo(url: String): Result<RepoMetadata> = fetchJson(url = url)

    suspend fun fetchPlugins(url: String): Result<List<PluginMetadata>> = fetchJson(url = url)

    suspend fun downloadPlugin(plugin: PluginMetadata): Result<InstalledPlugin> = withContext(
        Dispatchers.IO
    ) {
        try {
            val pluginsDir = File(context.filesDir, "plugins")
            if (!pluginsDir.exists()) {
                pluginsDir.mkdirs()
            }
            val file = File(pluginsDir, "${plugin.internalName}.cs3")
            if (!file.exists()) {
                file.createNewFile()
            }
            val request = Request.Builder().url(plugin.url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Download failed: ${response.code}"))
                }
                Result.success()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}