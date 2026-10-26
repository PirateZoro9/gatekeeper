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

// Main detailed page model (Supports both Movies and Series)
data class MediaDetails(
    val title: String,
    val posterUrl: String? = null,
    val genres: List<String> = emptyList(),
    val synopsis: String? = null,
    val rating: String? = null,
    val quality: List<String> = emptyList(),
    val seasons: List<Season> = emptyList()
) {
    // Automatically true if seasons are present, false for movies
    val isSeries: Boolean
        get() = seasons.isNotEmpty()
}

// Represents a Season containing its episodes
data class Season(
    val seasonNumber: Int,
    val seasonName: String = "Season $seasonNumber",
    val episodes: List<Episode> = emptyList()
)

// Represents a single Episode in a series
data class Episode(
    val episodeNumber: Int,
    val title: String = "Episode $episodeNumber"
)
