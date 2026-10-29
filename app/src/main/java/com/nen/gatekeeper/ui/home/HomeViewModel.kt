package com.nen.gatekeeper.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nen.gatekeeper.data.repo.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val result = mediaRepository.getHome()
            result.fold(
                onSuccess = { row ->
                    val cleanedRows = row.filter { it.rowCards.isNotEmpty() }
                    if (cleanedRows.isNotEmpty()) {
                        _uiState.value = HomeUiState.Success(cleanedRows)
                    } else {
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
