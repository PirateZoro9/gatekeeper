package com.nen.gatekeeper.data.api

import com.nen.gatekeeper.data.model.HomeRow
import com.nen.gatekeeper.data.model.MediaDetails
import com.nen.gatekeeper.data.model.StreamLink

/**
 * Standard template for all media sources.
 *
 * Every source implements this to load content, so the app
 * can use different websites without changing any UI code.
 */
interface MainApi {
    val name: String
    val mainUrl: String
    val lang: String

    // Fetches home screen movies in the background
    suspend fun getHome(): List<HomeRow>
    suspend fun getDetails(url: String): MediaDetails
    suspend fun loadStreams(dataUrl: String): List<StreamLink> = emptyList()
}
