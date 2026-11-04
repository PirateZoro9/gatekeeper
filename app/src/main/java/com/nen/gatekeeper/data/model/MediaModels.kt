package com.nen.gatekeeper.data.model

import kotlinx.serialization.Serializable

// Represents a media card on home or catalog pages
data class CardDetails(
    val title: String,
    val posterUrl: String? = null,
    val detailedUrl: String,
    val qualityTag: String? = null,
    val rating: String? = null,
    val releaseYear: String? = null
)

// Horizontal row of media cards on the home screen
data class HomeRow(
    val rowTitle: String,
    val rowCards: List<CardDetails>,
    val categoryUrl: String? = null
)

// Direct or intermediate stream/download link
data class StreamLink(
    val name: String,         // e.g., "HubCloud" or "Server 1"
    val quality: String,      // e.g., "1080p", "720p", "480p", "Unknown"
    val url: String,          // Stream or redirect URL
    val size: String? = null  // e.g., "1.4 GB"
) {
    val displayQuality: String
        get() = if (quality.contains("4k", ignoreCase = true) || quality.contains("2160")) {
            "2160p / 4K"
        } else {
            quality
        }
}

// Detailed media information for movies and series
data class MediaDetails(
    val title: String,
    val posterUrl: String? = null,
    val genres: List<String> = emptyList(),
    val synopsis: String? = null,
    val rating: String? = null,
    val streamLinks: List<StreamLink> = emptyList(),
    val seasons: List<Season> = emptyList()
) {
    val isSeries: Boolean get() = seasons.isNotEmpty()
}

// Season metadata containing episodes
data class Season(
    val seasonNumber: Int,
    val seasonName: String = if (seasonNumber == 0) "Specials" else "Season $seasonNumber",
    val episodes: List<Episode> = emptyList()
) {
    val isSpecial: Boolean get() = seasonNumber == 0
}

// Single episode metadata
data class Episode(
    val episodeNumber: Int,
    val title: String = "Episode $episodeNumber",
    val episodeUrl: String? = null,
    val episodeLinks: List<StreamLink> = emptyList()
)

@Serializable
data class VerifiedStreamData(
    val name: String,
    val url: String,
    val isSeekSupported: Boolean = false,
    val isAlive: Boolean = true,
    val headers: Map<String, String> = emptyMap(),
    val verifiedSize: String? = null,
    val totalBytes: Long? = null
)

@Serializable
data class PlayerMedia(
    val name: String,
    val currentStream: VerifiedStreamData,
    val allStreams: List<VerifiedStreamData> = emptyList()
)