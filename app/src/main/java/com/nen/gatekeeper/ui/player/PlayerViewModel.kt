package com.nen.gatekeeper.ui.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.mkv.MatroskaExtractor
import com.nen.gatekeeper.data.model.PlayerMedia
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

@OptIn(UnstableApi::class)
@HiltViewModel(assistedFactory = PlayerViewModel.Factory::class)
class PlayerViewModel @AssistedInject constructor(
    @ApplicationContext private val context: Context,
    private val client: OkHttpClient,
    @Assisted private val media: PlayerMedia
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(media: PlayerMedia): PlayerViewModel
    }

    private val _uiState = MutableStateFlow(
        PlayerUiState(
            title = media.name,
            isBuffering = true,
            isPlaying = true,
            isSeekSupported = media.currentStream.isSeekSupported
        )
    )

    val uiState = _uiState.asStateFlow()

    private val dataSourceFactory =
        OkHttpDataSource.Factory(client).setDefaultRequestProperties(media.currentStream.headers)

    private val extractorsFactory = DefaultExtractorsFactory().apply {
        if (!media.currentStream.isSeekSupported) {
            setMatroskaExtractorFlags(MatroskaExtractor.FLAG_DISABLE_SEEK_FOR_CUES)
        }
        setConstantBitrateSeekingEnabled(true)
    }

    private val mediaSourceFactory =
        DefaultMediaSourceFactory(context, extractorsFactory).setDataSourceFactory(dataSourceFactory)

    val player: ExoPlayer =
        ExoPlayer.Builder(context).setMediaSourceFactory(mediaSourceFactory).build()

    init {
        val isMkv = media.currentStream.name.contains(".mkv", ignoreCase = true) ||
                media.name.contains(".mkv", ignoreCase = true) ||
                media.currentStream.url.contains(".mkv", ignoreCase = true)
        val isMp4 = media.currentStream.name.contains(".mp4", ignoreCase = true) ||
                media.name.contains(".mp4", ignoreCase = true) ||
                media.currentStream.url.contains(".mp4", ignoreCase = true)
        val isHls = media.currentStream.name.contains(".m3u8", ignoreCase = true) ||
                media.currentStream.url.contains(".m3u8", ignoreCase = true)

        val mediaItem = MediaItem.Builder()
            .setUri(media.currentStream.url)
            .apply {
                when {
                    isMkv -> setMimeType(MimeTypes.VIDEO_MATROSKA)
                    isMp4 -> setMimeType(MimeTypes.VIDEO_MP4)
                    isHls -> setMimeType(MimeTypes.APPLICATION_M3U8)
                }
            }
            .build()
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val isBuffering = (playbackState == Player.STATE_BUFFERING)
                _uiState.update {
                    it.copy(
                        isBuffering = isBuffering,
                        durationMs = player.duration.coerceAtLeast(0L)
                    )
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.update {
                    it.copy(
                        isPlaying = isPlaying
                    )
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _uiState.update {
                    it.copy(
                        isBuffering = false,
                        errorMessage = error.localizedMessage ?: "Playback Error"
                    )
                }
            }
        })

        viewModelScope.launch {
            while (true) {
                if (player.isPlaying) {
                    _uiState.update {
                        it.copy(
                            currentPositionMs = player.currentPosition.coerceAtLeast(0L),
                            durationMs = player.duration.coerceAtLeast(0L)
                        )
                    }
                }
                delay(500)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}