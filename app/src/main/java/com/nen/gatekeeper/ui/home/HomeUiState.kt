package com.nen.gatekeeper.ui.home

import androidx.compose.runtime.Immutable
import com.nen.gatekeeper.data.model.HomeRow

@Immutable
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val rows: List<HomeRow>) : HomeUiState
    data class Error(val message: String) : HomeUiState
    data object Empty : HomeUiState
}
