package com.nen.gatekeeper.ui.home

import com.nen.gatekeeper.data.model.HomeRow

/**
 * Represents the different UI states of the home screen.
 */
sealed interface HomeUiState {

    // Content is being fetched in the background
    data object Loading : HomeUiState

    // Content loaded successfully with movie rows
    data class Success(val rows: List<HomeRow>) : HomeUiState

    // Failed to fetch content with an error message
    data class Error(val message: String) : HomeUiState
}