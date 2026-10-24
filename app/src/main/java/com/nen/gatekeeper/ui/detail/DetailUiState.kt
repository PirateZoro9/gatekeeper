package com.nen.gatekeeper.ui.detail

import com.nen.gatekeeper.data.model.MediaDetails

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Success(val detailPage: MediaDetails) : DetailUiState

    data class Error(val message: String) : DetailUiState

    data object Empty : DetailUiState
}