package com.nen.gatekeeper.data.model

import kotlinx.serialization.Serializable

// Metadata parsed from a community repository manifest (repo.json)
@Serializable
data class RepoMetadata(
    val name: String,                                 // Display name of the repository
    val description: String? = null,                  // Brief repository summary
    val manifestVersion: Int = 1,                     // Schema version
    val pluginLists: List<String> = emptyList()       // Direct URLs pointing to plugins.json
)

// Metadata parsed from plugins.json representing an available downloadable plugin
@Serializable
data class PluginMetadata(
    val name: String,                                 // Plugin display name (e.g. "FourKHDHub")
    val internalName: String,                         // Unique package/plugin identifier
    val version: Int,                                 // Incremental version code for updates
    val url: String,                                  // Direct download URL for the .cs3 file
    val iconUrl: String? = null,                      // Icon image preview URL
    val description: String? = null,                  // Short description of the plugin
    val authors: List<String> = emptyList(),          // Authors of the extension
    val tvTypes: List<String> = emptyList(),          // Categories supported (Movie, TvSeries)
    val fileSize: Long? = null                        // Download size in bytes
)

// Represents a locally installed .cs3 plugin stored in internal app sandbox storage
@Serializable
data class InstalledPlugin(
    val internalName: String,                         // Matches PluginMetadata.internalName
    val name: String,                                 // Plugin display name
    val version: Int,                                 // Installed version code
    val localFilePath: String,                        // Absolute disk path to .cs3 file
    val iconUrl: String? = null,                      // Cached icon URL
    val isEnabled: Boolean = true                     // Toggle state (active or disabled)
)