package com.nen.gatekeeper.data.repo

import com.nen.gatekeeper.data.api.MainApi
import com.nen.gatekeeper.data.model.HomeRow
import com.nen.gatekeeper.data.model.MediaDetails
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Repository that manages media data for the app.
 *
 * Sits between the data source and the ViewModel to clean,
 * filter, and safely handle errors before passing data to the UI.
 */
class MediaRepository @Inject constructor(private val api: MainApi) {

    // Fetches home screen data safely on a background thread
    suspend fun getHome(): Result<List<HomeRow>> = withContext(Dispatchers.IO) {
        try {
            val result = api.getHome()

            // Remove any duplicate movie cards that have the same link
            val cleanedResult: List<HomeRow> = result.map { row ->
                row.copy(
                    rowCards = row.rowCards.distinctBy {
                        it.detailedUrl
                    }
                )
            }

            Result.success(cleanedResult)
        } catch (e: CancellationException) {
            // Re-throw so coroutine cancellation works properly
            throw e
        } catch (e: Exception) {
            // Return failure safely instead of crashing the app
            Result.failure(e)
        }
    }

    // Fetches media details safely on a background thread
    suspend fun getDetails(url: String): Result<MediaDetails> = withContext(Dispatchers.IO) {
        if (url.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("URL cannot be blank"))
        }
        try {
            val details = api.getDetails(url)
            Result.success(details)
        } catch (e: CancellationException) {
            // Re-throw so coroutine cancellation works properly
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
