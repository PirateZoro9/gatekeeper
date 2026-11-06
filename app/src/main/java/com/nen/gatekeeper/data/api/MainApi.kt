package com.nen.gatekeeper.data.api

import com.nen.gatekeeper.data.model.HomeRow
import com.nen.gatekeeper.data.model.MediaDetails
import com.nen.gatekeeper.data.model.StreamLink

// Interface for media provider plugins
abstract class MainApi() {
    abstract val name: String
    abstract val mainUrl: String
    open val lang: String = "en"

    abstract suspend fun getHome(): List<HomeRow>
    abstract suspend fun getDetails(url: String): MediaDetails
    open suspend fun loadStreams(dataUrl: String): List<StreamLink> = emptyList()
}
