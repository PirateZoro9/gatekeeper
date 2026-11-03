package com.nen.gatekeeper.ui.detail

import androidx.compose.runtime.Immutable
import com.nen.gatekeeper.data.model.MediaDetails
import com.nen.gatekeeper.data.model.VerifiedStreamData

@Immutable
sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Success(val detailPage: MediaDetails) : DetailUiState

    data class Error(val message: String) : DetailUiState

    data object Empty : DetailUiState
}

@Immutable
sealed interface StreamUiState {
    data object Idle : StreamUiState
    data class Loading(
        val progress: Float = 0f,
        val statusText: String = "",
        val streamCountText: String = ""
    ) : StreamUiState

    data class Success(val streams: List<VerifiedStreamData>) : StreamUiState
    data class Error(val message: String) : StreamUiState
}
