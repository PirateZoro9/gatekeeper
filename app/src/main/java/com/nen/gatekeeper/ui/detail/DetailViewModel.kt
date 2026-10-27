package com.nen.gatekeeper.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nen.gatekeeper.data.repo.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(private val mediaRepository: MediaRepository, private val detailUrl: String) :
    ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    fun loadDetails() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            val result = mediaRepository.getDetails(detailUrl)
            result.fold(
                onSuccess = { data ->
                    if (data.title.isNotBlank()) {
                        _uiState.value = DetailUiState.Success(data)
                    } else {
                        _uiState.value = DetailUiState.Empty
                    }
                },
                onFailure = {
                    _uiState.value = DetailUiState.Error(
                        "Failed to load content. Please try again later."
                    )
                }
            )
        }
    }
}