package com.lagradost.cloudstream3

// Represents media cards displayed in Home Screen rows and Search Results.
// Plugins return these cards when scraping catalogs or search queries.

// Video resolution labels for media cards
enum class SearchQuality {
    None,
    Cam,
    CamRip,
    HdCam,
    Telesync,
    Telecine,
    Scr,
    DvdScr,
    Dvd,
    Vcd,
    HD,
    FHD,
    UHD,
    BluRay,
    FourK
}

// Common contract required for any media card on screen
interface SearchResponse {
    val name: String                         // Title of the movie or series
    val url: String                          // Webpage link to open details
    val apiName: String                      // Name of the provider plugin (e.g. "FourKHDHub")
    var type: TvType?                        // Content type (Movie, TvSeries, etc.)
    var posterUrl: String?                   // Thumbnail poster image URL
    var id: Int?                             // Optional unique ID
    var quality: SearchQuality?              // Optional quality tag (e.g. HD, FourK)
    var posterHeaders: Map<String, String>?  // Optional HTTP headers needed to load poster image
}

// Media card specifically for Movies
data class MovieSearchResponse(
    override val name: String,
    override val url: String,
    override val apiName: String,
    override var type: TvType? = TvType.Movie,
    override var posterUrl: String? = null,
    override var id: Int? = null,
    override var quality: SearchQuality? = null,
    override var posterHeaders: Map<String, String>? = null,
    var year: Int? = null                    // Release year of the movie
) : SearchResponse

// Media card specifically for TV Series
data class TvSeriesSearchResponse(
    override val name: String,
    override val url: String,
    override val apiName: String,
    override var type: TvType? = TvType.TvSeries,
    override var posterUrl: String? = null,
    override var id: Int? = null,
    override var quality: SearchQuality? = null,
    override var posterHeaders: Map<String, String>? = null,
    var year: Int? = null,                   // Release year of the series
    var latestEpisode: Int? = null           // Latest released episode number
) : SearchResponse

// Media card specifically for Anime
data class AnimeSearchResponse(
    override val name: String,
    override val url: String,
    override val apiName: String,
    override var type: TvType? = TvType.Anime,
    override var posterUrl: String? = null,
    override var id: Int? = null,
    override var quality: SearchQuality? = null,
    override var posterHeaders: Map<String, String>? = null,
    var year: Int? = null
) : SearchResponse
