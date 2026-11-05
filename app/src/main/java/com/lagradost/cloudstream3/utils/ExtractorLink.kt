package com.lagradost.cloudstream3.utils

// Represents direct playable video streams extracted by plugins.
// Passed to GateKeeper's Stream Verifier Engine to test HTTP 206 before playback in ExoPlayer.

// Streaming format classification for the video player engine
enum class ExtractorLinkType {
    M3U8,                                    // HLS segmented stream playlist (.m3u8)
    VIDEO,                                   // Progressive direct video file (.mp4, .mkv)
    DASH                                     // Dynamic adaptive streaming manifest (.mpd)
}

// Resolution presets used by providers to label video quality
enum class Qualities(val value: Int) {
    Unknown(400),
    P144(144),
    P240(240),
    P360(360),
    P480(480),
    P720(720),
    P1080(1080),
    P1440(1440),
    P2160(2160)
}

// Subtitle track associated with a video stream
data class SubtitleFile(
    val lang: String,                        // Subtitle language (e.g. "English", "Hindi")
    val url: String                          // Direct subtitle file link (.srt, .vtt)
)

// Concrete video stream container with direct URL and HTTP playback headers
open class ExtractorLink(
    val source: String,                      // Scraper or host name (e.g. "HubCloud", "StreamWish")
    val name: String,                        // Display server name (e.g. "Server 1 [1080p]")
    val url: String,                         // Direct playable stream URL
    val referer: String,                     // HTTP Referer header required to prevent 403 Forbidden
    val quality: Int,                        // Resolution integer (e.g. 1080, 720)
    val type: ExtractorLinkType = ExtractorLinkType.VIDEO, // Stream format (VIDEO or M3U8)
    val headers: Map<String, String> = emptyMap(),         // Extra playback HTTP headers (User-Agent, Origin)
    val extractorData: String? = null        // Optional internal extractor state
) {
    // Secondary constructor for older plugins that pass isM3u8 boolean instead of ExtractorLinkType enum
    constructor(
        source: String,
        name: String,
        url: String,
        referer: String,
        quality: Int,
        isM3u8: Boolean = false,
        headers: Map<String, String> = emptyMap(),
        extractorData: String? = null
    ) : this(
        source = source,
        name = name,
        url = url,
        referer = referer,
        quality = quality,
        type = if (isM3u8) ExtractorLinkType.M3U8 else ExtractorLinkType.VIDEO,
        headers = headers,
        extractorData = extractorData
    )
}
