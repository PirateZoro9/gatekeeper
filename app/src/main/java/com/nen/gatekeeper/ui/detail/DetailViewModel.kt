package com.nen.gatekeeper.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nen.gatekeeper.data.model.VerifiedStreamData
import com.nen.gatekeeper.data.repo.MediaRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.Request

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
    private val client = OkHttpClient()

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
    fun verifyStreams(urls: List<String>) {
        viewModelScope.launch {
            _streamState.value = StreamUIState.Loading
            val semaphore = Semaphore(4)
            val gigabyte = 1024.0 * 1024 * 1024
            val megabyte = 1024.0 * 1024
            val resolveTasks = urls.map { url ->
                async {
                    semaphore.withPermit {
                        mediaRepository.getStreams(url)
                    }
                }
            }
            val rawResults = resolveTasks.awaitAll()
            val directStreams = rawResults.mapNotNull {
                it.getOrNull()
            }.flatten()

            val streamHeaders = mapOf(
                "User-Agent" to "Mozilla/5.0 (Linux; Android 14; Pixel 8 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.6613.146 Mobile Safari/537.36",
                "Accept" to "*/*",
                "Accept-Language" to "en-US,en;q=0.9",
                "Sec-CH-UA-Mobile" to "?1",
                "Sec-CH-UA-Platform" to "\"Android\""
            )
            val requestBuilder = Request.Builder()
                .headers(streamHeaders.toHeaders())
                .header("Range", "bytes=0-1")

            val verifiedStreams = withContext(Dispatchers.IO) {
                val testTasks = directStreams.map { streamLink ->
                    async {
                        semaphore.withPermit {
                            try {
                                val request = requestBuilder.url(streamLink.url).build()
                                val response = client.newCall(request).execute()
                                response.use {
                                    val finalPlayableUrl = it.request.url.toString()
                                    val totalBytes = it.header("Content-Range")?.substringAfter("/")
                                        ?.toLongOrNull()
                                    val sizeString = if (totalBytes != null) {
                                        if (totalBytes >= gigabyte) {
                                            String.format(
                                                java.util.Locale.US,
                                                "%.2f GB",
                                                totalBytes / gigabyte
                                            )
                                        } else {
                                            String.format(
                                                java.util.Locale.US,
                                                "%.0f MB",
                                                totalBytes / megabyte
                                            )
                                        }
                                    } else {
                                        streamLink.size
                                    }
                                    VerifiedStreamData(
                                        name = streamLink.name,
                                        url = finalPlayableUrl,
                                        isSeekSupported = (it.code == 206),
                                        isAlive = (it.code == 206 || it.code == 200),
                                        headers = streamHeaders,
                                        verifiedSize = sizeString
                                    )
                                }
                            } catch (e: Exception) {
                                VerifiedStreamData(
                                    name = streamLink.name,
                                    url = streamLink.url,
                                    isSeekSupported = false,
                                    isAlive = false
                                )
                            }
                        }
                    }
                }
                testTasks.awaitAll()
            }
            if (verifiedStreams.isNotEmpty()) {
                _streamState.value = StreamUIState.Success(verifiedStreams)
            } else {
                _streamState.value = StreamUIState.Error("No working servers found")
            }
        }
    }
}