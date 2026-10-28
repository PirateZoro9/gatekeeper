package com.nen.gatekeeper.data.model

/**
 * Data models used to display content on the UI.
 *
 * Defines the structure for individual movie cards,
 * horizontal home rows, and detailed media pages.
 */

// Represents a single movie or show card
data class CardDetails(
    val title: String,
    val posterUrl: String? = null,
    val detailedUrl: String,
    val qualityTag: String? = null,
    val rating: String? = null,
    val releaseYear: String? = null
)

// Represents a horizontal row of movies on the home screen
data class HomeRow(
    val rowTitle: String,              // Section heading (e.g., "Latest Releases")
    val rowCards: List<CardDetails>,   // List of movie cards inside this row
    val categoryUrl: String? = null    // Web link or path for "See All" full category view
)

// A flat, simple stream/download link (No nesting!)
data class StreamLink(
    val name: String,         // "HubCloud" ya "1080p HEVC"
    val quality: String,      // "1080p", "720p", "480p", "Unknown"
    val url: String,          // Redirect link (greenmotors / hubcloud)
    val size: String? = null  // "1.4 GB" (agar mile toh)
)

// Main detailed page model (Supports both Movies and Series)
data class MediaDetails(
    val title: String,
    val posterUrl: String? = null,
    val genres: List<String> = emptyList(),
    val synopsis: String? = null,
    val rating: String? = null,
    val movieLinks: List<StreamLink> = emptyList(),
    val seasons: List<Season> = emptyList()
) {
    // Automatically true if seasons are present, false for movies
    val isSeries: Boolean
        get() = seasons.isNotEmpty()
}

// Represents a Season containing its episodes
data class Season(
    val seasonNumber: Int,
    val seasonName: String = if (seasonNumber == 0) "Specials" else "Season $seasonNumber",
    val episodes: List<Episode> = emptyList()
) {
    val isSpecial: Boolean get() = seasonNumber == 0
}

// Represents a single Episode in a series
data class Episode(
    val episodeNumber: Int,
    val title: String = "Episode $episodeNumber",
    val episodeUrl: String? = null,
    val episodeLinks: List<StreamLink> = emptyList()
)