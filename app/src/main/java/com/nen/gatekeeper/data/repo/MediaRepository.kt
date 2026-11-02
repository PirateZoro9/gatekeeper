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
        safeApiCall {
            api.getHome().map { row ->
                row.copy(rowCards = row.rowCards.distinctBy { it.detailedUrl })
            }
        }
    }

    // Fetch media details by url
    suspend fun getDetails(url: String): Result<MediaDetails> = withContext(Dispatchers.IO) {
        if (url.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("URL missing or expired"))
        }
        safeApiCall { api.getDetails(url) }
    }

    suspend fun getStreams(url: String): Result<List<StreamLink>> = withContext(Dispatchers.IO) {
        if (url.isBlank()) {
            return@withContext Result.failure(exception = IllegalArgumentException("URL missing or expired"))
        }
        safeApiCall {
            api.loadStreams(url)
        }
    }

    private inline fun <T> safeApiCall(block: () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }
}
