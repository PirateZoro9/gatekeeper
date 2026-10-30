package com.nen.gatekeeper.data.repo

import com.nen.gatekeeper.data.api.MainApi
import com.nen.gatekeeper.data.model.HomeRow
import com.nen.gatekeeper.data.model.MediaDetails
import com.nen.gatekeeper.data.model.StreamLink
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Repository managing media operations and data transformation
class MediaRepository @Inject constructor(private val api: MainApi) {

    // Fetch and deduplicate home rows
    suspend fun getHome(): Result<List<HomeRow>> = withContext(Dispatchers.IO) {
        try {
            val result = api.getHome()
            val cleanedResult = result.map { row ->
                row.copy(rowCards = row.rowCards.distinctBy { it.detailedUrl })
            }
            Result.success(cleanedResult)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Fetch media details by url
    suspend fun getDetails(url: String): Result<MediaDetails> = withContext(Dispatchers.IO) {
        if (url.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("URL missing or expired"))
        }
        try {
            val details = api.getDetails(url)
            Result.success(details)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStreams(url: String): Result<List<StreamLink>> = withContext(Dispatchers.IO) {
        if (url.isBlank()) {
            return@withContext Result.failure(exception = IllegalArgumentException("URL missing or expired"))
        }
        try {
            val streamLinks = api.loadStreams(url)
            Result.success(streamLinks)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }
}
