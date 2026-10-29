package com.nen.gatekeeper.data.api

import com.nen.gatekeeper.data.model.HomeRow
import com.nen.gatekeeper.data.model.MediaDetails
import com.nen.gatekeeper.data.model.StreamLink

// Interface for media provider plugins
interface MainApi {
    val name: String
    val mainUrl: String
    val lang: String

    suspend fun getHome(): List<HomeRow>
    suspend fun getDetails(url: String): MediaDetails
    suspend fun loadStreams(dataUrl: String): List<StreamLink> = emptyList()
}
