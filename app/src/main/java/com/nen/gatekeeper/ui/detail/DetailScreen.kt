package com.nen.gatekeeper.ui.detail

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nen.gatekeeper.data.model.MediaDetails
import com.nen.gatekeeper.data.model.PlayerMedia
import com.nen.gatekeeper.data.model.StreamLink
import com.nen.gatekeeper.ui.components.EmptyState
import com.nen.gatekeeper.ui.components.ErrorState
import com.nen.gatekeeper.ui.components.StreamSelectorSheet
import com.nen.gatekeeper.ui.theme.AppSpacing

// Media detail screen with hero, meta, seasons, and stream sheet
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
    onPlayClick: (PlayerMedia) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val streamState by viewModel.streamState.collectAsStateWithLifecycle()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Crossfade(targetState = uiState, animationSpec = tween(500)) { state ->
                when (state) {
                    is DetailUiState.Error -> {
                        ErrorState(message = state.message, onRetry = { viewModel.loadDetails() })
                    }

                    is DetailUiState.Empty -> {
                        EmptyState()
                    }

                    is DetailUiState.Success -> {
                        DetailContent(
                            mediaDetail = state.detailPage,
                            contentPadding = contentPadding,
                            streamState = streamState,
                            onVerifyClick = { urls -> viewModel.verifyStreams(urls) },
                            onResetVerifier = { viewModel.resetStreamVerifier() },
                            onPlayClick = { media ->
                                onPlayClick(media)
                            }
                        )
                    }

                    is DetailUiState.Loading -> {
                        DetailShimmer()
                    }
                }
            }
        }
        DetailTopBar(
            onBackClick = onBackClick,
            contentPadding = contentPadding
        )
    }
}

// Main detail scrollable content
@Composable
fun DetailContent(
    mediaDetail: MediaDetails,
    contentPadding: PaddingValues,
    streamState: StreamUiState,
    onVerifyClick: (List<String>) -> Unit,
    onResetVerifier: () -> Unit,
    onPlayClick: (PlayerMedia) -> Unit
) {
    var showStreamSheet by remember {
        mutableStateOf(false)
    }
    var activeSheetTitle by remember {
        mutableStateOf("")
    }
    var activeStreamLinks by remember {
        mutableStateOf<List<StreamLink>>(emptyList())
    }
    LazyColumn(
        contentPadding = PaddingValues(
            bottom = 24.dp + contentPadding.calculateBottomPadding()
        )
    ) {
        // Hero header
        item(key = "hero_header") {
            DetailHeroHeader(
                posterUrl = mediaDetail.posterUrl,
                title = mediaDetail.title,
                rating = mediaDetail.rating,
                onPlayClick = {
                    onResetVerifier()
                    activeSheetTitle = mediaDetail.title
                    activeStreamLinks = mediaDetail.streamLinks
                    showStreamSheet = true
                }
            )
        }

        // Genres
        item(key = "genre_chips") {
            DetailGenreChips(
                genres = mediaDetail.genres,
                modifier = Modifier.padding(top = AppSpacing.cardGap)
            )
        }

        // Synopsis
        item(key = "synopsis") {
            mediaDetail.synopsis?.let { synopsisText ->
                DetailSynopsisSection(synopsis = synopsisText)
            }
        }

        // Series: Seasons & Episodes
        item(key = "series_section") {
            if (mediaDetail.isSeries) {
                DetailSeriesSection(
                    mediaDetail.seasons,
                    posterUrl = mediaDetail.posterUrl,
                    onEpisodeClick = { episode, seasonBadge ->
                        onResetVerifier()
                        activeSheetTitle = "${mediaDetail.title} • $seasonBadge"
                        activeStreamLinks = episode.episodeLinks
                        showStreamSheet = true
                    }
                )
            }
        }
    }

    // Stream and quality selection modal sheet
    StreamSelectorSheet(
        title = activeSheetTitle,
        streamLink = activeStreamLinks,
        isOpen = showStreamSheet,
        onDismiss = {
            showStreamSheet = false
            onResetVerifier()
        },
        onVerifyClick = { urls ->
            onVerifyClick(urls)
        },
        streamUIState = streamState,
        onPlayClick = { media ->
            onPlayClick(media)
        }
    )
}
