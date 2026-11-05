package com.lagradost.cloudstream3

// Represents Home Screen layout structures returned by plugins.
// Equivalent to GateKeeper's HomeRow architecture.

// A single row of media cards on the Home screen
data class HomePageList(
    val name: String,                        // Section title (e.g. "Trending Now", "Latest Series")
    val list: List<SearchResponse>,          // List of media cards in this row
    val isHorizontalList: Boolean = true     // True for horizontal scroll row; false for vertical grid
)

// Complete Home screen response wrapping all rows
data class HomePageResponse(
    val items: List<HomePageList>,           // All rows to display on the Home screen
    val hasNext: Boolean = false             // True if more pages exist for infinite scroll
)
