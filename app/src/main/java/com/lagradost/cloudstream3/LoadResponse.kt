package com.lagradost.cloudstream3

// Represents Detail Screen data returned by plugins when a user taps a card.
// Contains synopsis, genres, poster, and playable episodes/seasons.

// Common contract for media detail information
interface LoadResponse {
    val name: String                         // Title of the movie or series
    val url: String                          // Source webpage URL
    val apiName: String                      // Name of the provider plugin
    var type: TvType                         // Content type (Movie, TvSeries, etc.)
    var posterUrl: String?                   // Main poster image URL
    var year: Int?                           // Release year
    var plot: String?                        // Synopsis / Story description
    var tags: List<String>?                  // Genres (e.g. Action, Sci-Fi)
    var rating: Int?                         // Community rating
    var posterHeaders: Map<String, String>?  // HTTP headers needed to load poster
}

// Single episode data entry inside a TV series or anime
data class Episode(
    var data: String,                        // Stream lookup link passed to provider to extract links
    var name: String? = null,                // Episode title (e.g. "Episode 1" or "Chapter One")
    var season: Int? = null,                 // Season number (1, 2, 3...)
    var episode: Int? = null,                // Episode index number
    var posterUrl: String? = null,           // Episode preview thumbnail
    var rating: Int? = null,                 // Episode rating
    var description: String? = null          // Episode summary
)

// Detail screen data for a standalone Movie
data class MovieLoadResponse(
    override val name: String,
    override val url: String,
    override val apiName: String,
    override var type: TvType = TvType.Movie,
    var dataUrl: String,                     // Link used to extract movie video streams
    override var posterUrl: String? = null,
    override var year: Int? = null,
    override var plot: String? = null,
    override var tags: List<String>? = null,
    override var rating: Int? = null,
    override var posterHeaders: Map<String, String>? = null
) : LoadResponse

// Detail screen data for a multi-episode TV Series
data class TvSeriesLoadResponse(
    override val name: String,
    override val url: String,
    override val apiName: String,
    override var type: TvType = TvType.TvSeries,
    var episodes: List<Episode> = emptyList(), // All episodes across all seasons
    override var posterUrl: String? = null,
    override var year: Int? = null,
    override var plot: String? = null,
    override var tags: List<String>? = null,
    override var rating: Int? = null,
    override var posterHeaders: Map<String, String>? = null
) : LoadResponse

// Detail screen data for Anime shows
data class AnimeLoadResponse(
    override val name: String,
    override val url: String,
    override val apiName: String,
    override var type: TvType = TvType.Anime,
    var episodes: List<Episode> = emptyList(),
    override var posterUrl: String? = null,
    override var year: Int? = null,
    override var plot: String? = null,
    override var tags: List<String>? = null,
    override var rating: Int? = null,
    override var posterHeaders: Map<String, String>? = null
) : LoadResponse
