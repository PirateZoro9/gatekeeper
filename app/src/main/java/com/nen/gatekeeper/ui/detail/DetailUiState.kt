package com.nen.gatekeeper.ui.detail

import com.nen.gatekeeper.data.model.MediaDetails
import com.nen.gatekeeper.data.model.StreamLink

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Success(val detailPage: MediaDetails) : DetailUiState

    data class Error(val message: String) : DetailUiState

    data object Empty : DetailUiState
}

sealed interface StreamUIState {
    data object Idle : StreamUIState
    data object Loading : StreamUIState
    data class Success(val streams: List<StreamLink>) : StreamUIState
    data class Error(val message: String) : StreamUIState
}