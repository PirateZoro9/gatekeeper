package com.nen.gatekeeper.ui.detail

import com.nen.gatekeeper.data.model.MediaDetails
import com.nen.gatekeeper.data.model.VerifiedStreamData

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Success(val detailPage: MediaDetails) : DetailUiState

    data class Error(val message: String) : DetailUiState

    data object Empty : DetailUiState
}

sealed interface StreamUIState {
    data object Idle : StreamUIState
    data class Loading(
        val progress: Float = 0f,
        val statusText: String = "",
        val streamCountText: String = ""
    ) : StreamUIState

    data class Success(val streams: List<VerifiedStreamData>) : StreamUIState
    data class Error(val message: String) : StreamUIState
}
