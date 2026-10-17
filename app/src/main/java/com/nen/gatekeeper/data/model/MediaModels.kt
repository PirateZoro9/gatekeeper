package com.nen.gatekeeper.data.model

/**
 * Data models used to display content on the UI.
 *
 * Defines the structure for individual movie cards
 * and the horizontal rows shown on the home screen.
 */

// Represents a single movie or show card
data class MediaDetails(
    val title: String,              // Movie or show title
    val posterUrl: String? = null,  // Image URL for the poster (optional if missing)
    val detailedUrl: String         // Web link to open the movie details page
)

// Represents a horizontal row of movies on the home screen
data class HomeRow(
    val rowTitle: String,              // Section heading (e.g., "Latest Releases")
    val rowCards: List<MediaDetails>   // List of movie cards inside this row
)