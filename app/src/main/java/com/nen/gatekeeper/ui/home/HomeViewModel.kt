package com.nen.gatekeeper.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nen.gatekeeper.data.repo.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the home screen.
 *
 * Fetches movie rows from the repository and exposes the UI state
 * so the screen knows whether to show loading, error, or content.
 */
class HomeViewModel(private val mediaRepository: MediaRepository) : ViewModel() {

    // Internal state flow that updates the current screen state
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    // Public read-only state observed by the UI
    val uiState = _uiState.asStateFlow()

    init {
        // Automatically fetch movies when the ViewModel is created
        loadHome()
    }

    // Fetches home screen data and updates uiState based on the result
    fun loadHome() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val result = mediaRepository.getHome()
            result.fold(
                onSuccess = { row ->
                    val cleanedRows = row.filter {
                        it.rowCards.isNotEmpty()
                    }
                    if(cleanedRows.isNotEmpty()){
                        _uiState.value = HomeUiState.Success(cleanedRows)
                    }else{
                        _uiState.value = HomeUiState.Empty
                    }
                },
                onFailure = {
                    _uiState.value = HomeUiState.Error(
                        "Failed to load content. Please try again later."
                    )
                }
            )
        }
    }
}