package com.nen.gatekeeper.ui.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nen.gatekeeper.data.model.Episode
import com.nen.gatekeeper.data.model.MediaDetails
import com.nen.gatekeeper.data.model.Season
import com.nen.gatekeeper.data.model.StreamLink
import com.nen.gatekeeper.ui.components.AppImage
import com.nen.gatekeeper.ui.components.EmptyState
import com.nen.gatekeeper.ui.components.ErrorState
import com.nen.gatekeeper.ui.components.StreamSelectorSheet
import com.nen.gatekeeper.ui.components.shimmerEffect
import com.nen.gatekeeper.ui.theme.AppRadius
import com.nen.gatekeeper.ui.theme.AppSpacing
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBackClick: () -> Unit,
    contentPadding: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
                            contentPadding = contentPadding
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

@Composable
fun DetailShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .clip(shape = RoundedCornerShape(AppRadius.hero))
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(AppSpacing.medium))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(48.dp)
                    .clip(shape = RoundedCornerShape(24.dp))
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                    .shimmerEffect()
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.large))

        Column(
            modifier = Modifier.padding(horizontal = AppSpacing.medium)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(20.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(14.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(AppSpacing.small))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(14.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                    .shimmerEffect()
            )
        }
    }
}

@Composable
fun DetailTopBar(
    onBackClick: () -> Unit,
    contentPadding: PaddingValues
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = contentPadding.calculateTopPadding() + 4.dp,
                start = 14.dp,
                end = 14.dp
            )
    ) {
        OutlinedIconButton(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(50))
                .background(
                    color = Color.Black.copy(alpha = 0.40f)
                )
                .size(40.dp),
            onClick = onBackClick,
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}

@Composable
fun DetailContent(
    mediaDetail: MediaDetails,
    contentPadding: PaddingValues
) {
    var showStreamSheet by rememberSaveable {
        mutableStateOf(false)
    }
    LazyColumn(
        contentPadding = PaddingValues(
            bottom = 24.dp + contentPadding.calculateBottomPadding()
        )
    ) {
        // Hero header
        item {
            DetailHeroHeader(
                posterUrl = mediaDetail.posterUrl,
                title = mediaDetail.title,
                rating = mediaDetail.rating,
                onPlayClick = {
                    showStreamSheet = true
                }
            )
        }

        // Genres
        item { DetailGenreChips(genres = mediaDetail.genres) }

        // Synopsis
        item {
            mediaDetail.synopsis?.let { synopsisText ->
                DetailSynopsisSection(synopsis = synopsisText)
            }
        }

        // Series: Seasons & Episodes
        item {
            if (mediaDetail.isSeries) {
                DetailSeriesSection(
                    mediaDetail.seasons,
                    posterUrl = mediaDetail.posterUrl
                )
            } else {
                DetailMovieSection(
                    movieLinks = mediaDetail.movieLinks,
                    playClicked = showStreamSheet,
                    modifier = Modifier,
                    contentPadding = contentPadding,
                    movieTitle = mediaDetail.title,
                    onDismissClick = { showStreamSheet = false }
                )
            }
        }
    }
}

// Hero header with poster, title, rating, and play button
@Composable
fun DetailHeroHeader(
    posterUrl: String?,
    title: String,
    rating: String?,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = MaterialTheme.colorScheme.background
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
            .clip(shape = RoundedCornerShape(AppRadius.hero))
    ) {
        AppImage(
            posterUrl = posterUrl,
            contentDescription = "Detailed Poster",
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            bg.copy(alpha = 0.60f),
                            bg.copy(alpha = 0.90f),
                            bg.copy(alpha = 1.0f),
                        ),
                        startY = size.height * 0.6f,
                        endY = size.height
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = brush,
                            topLeft = Offset(0f, size.height * 0.6f),
                            Size(size.width, size.height * 0.4f)
                        )
                    }
                }
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            rating?.let { ratingText ->
                Spacer(modifier = Modifier.height(6.dp))
                RatingBadge(ratingText = ratingText)
                Spacer(modifier = Modifier.height(6.dp))
            }
            Button(
                onClick = onPlayClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier.width(100.dp)
            ) {
                Text(text = "Play")
            }
        }
    }
}

// Rating badge
@Composable
fun RatingBadge(
    ratingText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(4.dp))
            .background(color = Color.White.copy(alpha = 0.12f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = ratingText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// Genre chips
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailGenreChips(
    genres: List<String>,
    modifier: Modifier = Modifier
) {
    genres.takeIf { it.isNotEmpty() }?.let { genreList ->
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            modifier = modifier.fillMaxWidth(),
            maxItemsInEachRow = 5,
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                Alignment.CenterHorizontally
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            genreList.take(7).forEach { genre ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(color = Color.White.copy(alpha = 0.05f))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.25f),
                            RoundedCornerShape(50)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = genre, fontSize = 12.sp)
                }
            }
        }
    }
}

// Expandable synopsis section
@Composable
fun DetailSynopsisSection(
    synopsis: String,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var showSeeMore by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.medium, vertical = AppSpacing.small)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 350,
                    easing = FastOutSlowInEasing
                )
            )
    ) {
        Text(
            text = synopsis,
            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { result ->
                if (!isExpanded && result.hasVisualOverflow) {
                    showSeeMore = true
                }
            },
            fontSize = 14.sp
        )
        if (showSeeMore) {
            TextButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier
                    .sizeIn(minHeight = 48.dp)
                    .semantics {
                        stateDescription = if (isExpanded) "Expanded" else "Collapsed"
                        onClick(
                            label = if (isExpanded) "Show less" else "Show more",
                            action = null
                        )
                    },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (isExpanded) "Show less" else "Show more",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun DetailMovieSection(
    movieLinks: List<StreamLink>,
    playClicked: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    movieTitle: String,
    onDismissClick: () -> Unit
) {
    StreamSelectorSheet(
        title = movieTitle,
        streamLink = movieLinks,
        isOpen = playClicked,
        onDismiss = onDismissClick
    )
}

@Composable
fun DetailSeriesSection(
    seasons: List<Season>,
    modifier: Modifier = Modifier,
    posterUrl: String?
) {
    var selectedSeasonNumber by rememberSaveable {
        mutableStateOf(
            seasons.firstOrNull()?.seasonNumber ?: 1
        )
    }
    val selectedSeason = seasons.find {
        it.seasonNumber == selectedSeasonNumber
    }
    val episodes = selectedSeason?.episodes.orEmpty()
    val episodeListState = rememberSaveable(selectedSeasonNumber, saver = LazyListState.Saver) {
        LazyListState()
    }
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (seasons.size > 1) {
                Text(
                    text = "Seasons",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(6.dp))
                    .background(Color(0xFFFF5C39).copy(alpha = 0.15f))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFFF5C39).copy(alpha = 0.30f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${episodes.size} Episodes Available",
                    color = Color(0xFFFF5C39),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(items = seasons) { season ->
                val isSelected = selectedSeason == season
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            color = if (isSelected) Color.White else Color.Black
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(50)
                        )
                        .clickable {
                            selectedSeasonNumber = season.seasonNumber
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = season.seasonName,
                        color = if (isSelected) Color.Black else Color.Gray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (episodes.isEmpty()) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "No episodes available"
            )
        } else {
            AnimatedContent(
                targetState = selectedSeasonNumber,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { fullWidth -> fullWidth } + fadeIn() togetherWith (slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut())
                    } else {
                        slideInHorizontally { fullWidth -> -fullWidth } + fadeIn() togetherWith slideOutHorizontally { fullWidth -> fullWidth } + fadeOut()
                    }
                }
            ) { targetSeasonNumber ->
                val currentSeason = seasons.find {
                    it.seasonNumber == targetSeasonNumber
                }
                val currentEpisodes = currentSeason?.episodes.orEmpty()
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 50.dp),
                    state = episodeListState
                ) {
                    items(items = currentEpisodes) { ep ->
                        EpisodeCard(
                            isSpecial = selectedSeason?.isSpecial == true,
                            seasonNumber = selectedSeason?.seasonNumber,
                            episode = ep,
                            posterUrl = posterUrl
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun EpisodeCard(
    isSpecial: Boolean = false,
    seasonNumber: Int?,
    episode: Episode,
    posterUrl: String?,
    modifier: Modifier = Modifier
) {
    val seasonBadge = when {
        isSpecial -> "SP ${episode.episodeNumber}"
        seasonNumber != null -> "S$seasonNumber E${episode.episodeNumber}"
        else -> "EP ${episode.episodeNumber}"
    }
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .width(210.dp)
            .aspectRatio(16f / 9.5f)
    ) {
        AppImage(
            posterUrl = posterUrl,
            contentDescription = "Detailed Ui",
            modifier = Modifier
                .fillMaxSize()
                .blur(2.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.40f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(color = Color.White.copy(alpha = 0.05f))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.25f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = seasonBadge,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = episode.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}