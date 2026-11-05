package com.lagradost.cloudstream3

import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.SubtitleFile

// Base configuration for a Home Screen category row.
// Plugins define these so the app knows which section URLs to scrape.
data class MainPageRequest(
    val name: String,                        // Section title (e.g. "Bollywood", "Trending Movies")
    val data: String,                        // Website URL or path to scrape (e.g. "/category/bollywood/")
    val horizontalImages: Boolean = false    // True for 16:9 wide banner posters; false for normal 2:3 vertical posters
)

// Base class inherited by all CloudStream scraper plugins.
// Plugins override these properties and functions to scrape websites.
abstract class MainAPI {
    open var name: String = "Provider"                    // Plugin display name
    open var mainUrl: String =
        ""                         // Base website URL (e.g. "https://Something.com")
    open val supportedTypes: Set<TvType> = setOf(TvType.Movie) // Media types provided by this site
    open var lang: String = "en"                          // Language code (e.g. "hi", "en")
    open val hasMainPage: Boolean =
        false                 // True if provider supports home screen catalogs
    open val hasQuickSearch: Boolean =
        false              // True if provider supports fast instant search

    // Fetches rows of media cards for the Home screen
    open suspend fun getMainPage(
        page: Int = 1,
        request: MainPageRequest? = null
    ): HomePageResponse? = null

    // Searches the provider website for a query text
    open suspend fun search(query: String): List<SearchResponse>? = null

    // Loads detail page metadata (synopsis, episodes, seasons) for a selected title
    open suspend fun load(url: String): LoadResponse? = null

    // Resolves and returns direct playable video stream links for a movie or episode
    open suspend fun loadLinks(
        data: String,
        isCasting: Boolean = false,
        subtitleCallback: (SubtitleFile) -> Unit = {},
        callback: (ExtractorLink) -> Unit
    ): Boolean = false

    // Helper builder used by plugins to create a movie search card
    fun newMovieSearchResponse(
        name: String,
        url: String,
        type: TvType = TvType.Movie,
        builder: MovieSearchResponse.() -> Unit = {}
    ): MovieSearchResponse {
        return MovieSearchResponse(name, url, this.name, type).apply(builder)
    }

    // Helper builder used by plugins to create a TV series search card
    fun newTvSeriesSearchResponse(
        name: String,
        url: String,
        type: TvType = TvType.TvSeries,
        builder: TvSeriesSearchResponse.() -> Unit = {}
    ): TvSeriesSearchResponse {
        return TvSeriesSearchResponse(name, url, this.name, type).apply(builder)
    }

    // Helper builder used by plugins to create an anime search card
    fun newAnimeSearchResponse(
        name: String,
        url: String,
        type: TvType = TvType.Anime,
        builder: AnimeSearchResponse.() -> Unit = {}
    ): AnimeSearchResponse {
        return AnimeSearchResponse(name, url, this.name, type).apply(builder)
    }

    // Helper builder used by plugins to create movie detail responses
    fun newMovieLoadResponse(
        name: String,
        url: String,
        type: TvType = TvType.Movie,
        dataUrl: String,
        builder: MovieLoadResponse.() -> Unit = {}
    ): MovieLoadResponse {
        return MovieLoadResponse(name, url, this.name, type, dataUrl).apply(builder)
    }

    // Helper builder used by plugins to create TV series detail responses
    fun newTvSeriesLoadResponse(
        name: String,
        url: String,
        type: TvType = TvType.TvSeries,
        episodes: List<Episode> = emptyList(),
        builder: TvSeriesLoadResponse.() -> Unit = {}
    ): TvSeriesLoadResponse {
        return TvSeriesLoadResponse(name, url, this.name, type, episodes).apply(builder)
    }

    // Helper builder used by plugins to create anime detail responses
    fun newAnimeLoadResponse(
        name: String,
        url: String,
        type: TvType = TvType.Anime,
        episodes: List<Episode> = emptyList(),
        builder: AnimeLoadResponse.() -> Unit = {}
    ): AnimeLoadResponse {
        return AnimeLoadResponse(name, url, this.name, type, episodes).apply(builder)
    }
}
