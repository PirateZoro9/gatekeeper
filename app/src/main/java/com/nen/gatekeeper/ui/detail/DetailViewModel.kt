package com.nen.gatekeeper.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nen.gatekeeper.data.repo.MediaRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DetailViewModel.Factory::class)
class DetailViewModel @AssistedInject constructor(
    private val mediaRepository: MediaRepository,
    @Assisted private val detailUrl: String
) :
    ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(detailUrl: String): DetailViewModel
    }

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

    private val _streamState = MutableStateFlow<StreamUIState>(StreamUIState.Idle)
    val streamState = _streamState.asStateFlow()

    fun loadStreams(url: String) {
        viewModelScope.launch {
            _streamState.value = StreamUIState.Loading
            val result = mediaRepository.getStreams(url)
            result.fold(
                onSuccess = { streams ->
                    _streamState.value = StreamUIState.Success(streams)
                },
                onFailure = { error ->
                    _streamState.value =
                        StreamUIState.Error(error.message ?: "Failed to resolve stream")
                }
            )
        }
    }
}