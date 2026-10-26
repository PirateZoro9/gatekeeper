package com.nen.gatekeeper.ui.detail

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nen.gatekeeper.data.model.MediaDetails
import com.nen.gatekeeper.data.model.Season
import com.nen.gatekeeper.ui.components.EmptyState
import com.nen.gatekeeper.ui.components.ErrorState
import com.nen.gatekeeper.ui.components.PosterImage
import com.nen.gatekeeper.ui.components.shimmerEffect
import com.nen.gatekeeper.ui.theme.AppRadius
import com.nen.gatekeeper.ui.theme.AppSpacing

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
                        DetailContent(state.detailPage)
                    }

                    is DetailUiState.Loading -> {
                        DetailShimmer()
                    }
                }
            }
        }
        DetailTopBar(onBackClick = onBackClick)
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
fun DetailTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp)
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
    mediaDetail: MediaDetails
) {
    LazyColumn {
        // Hero poster with gradient, title, rating badge, and play button
        item {
            DetailHeroHeader(
                posterUrl = mediaDetail.posterUrl,
                title = mediaDetail.title,
                rating = mediaDetail.rating,
                onPlayClick = { /* Play clicked */ }
            )
        }

        // Genre badges flow row
        item { DetailGenreChips(genres = mediaDetail.genres) }

        // Synopsis text with smooth expand / collapse animation
        item {
            mediaDetail.synopsis?.let { synopsisText ->
                DetailSynopsisSection(synopsis = synopsisText)
            }
        }

        // Series Section: Seasons & Episodes (for web series)
        item {
            if (mediaDetail.isSeries) {
                // Seasons and episodes will be rendered here
                DetailSeriesSection(
                    mediaDetail.seasons,
                    posterUrl = mediaDetail.posterUrl
                )
            }
        }
    }
}

// Hero poster with vertical bottom fade, movie title, rating badge, and play button
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
        PosterImage(
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

// Compact rating badge with golden star icon and crisp rating text
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

// Centered FlowRow displaying genre badges
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

// Expandable synopsis block with smooth height transition
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
fun DetailSeriesSection(
    seasons: List<Season>,
    modifier: Modifier = Modifier,
    posterUrl: String?
) {
    var selectedSeason by remember {
        mutableStateOf(
            seasons.firstOrNull()
        )
    }
    val episodes = selectedSeason?.episodes.orEmpty()
    val bg = MaterialTheme.colorScheme.background
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (seasons.size > 1) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Seasons",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            items(items = seasons) { season ->
                val isSelected = selectedSeason == season
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            color = if (isSelected) Color.White.copy(alpha = 0.10f) else Color.Black
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(50)
                        )
                        .clickable {
                            selectedSeason = season
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = season.seasonName,
                        color = if (isSelected) Color.White else Color.Gray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
        Spacer(modifier = modifier.height(12.dp))
        if (episodes.isEmpty()) {
            Text(
                text = "No episodes available"
            )
        } else {
            LazyRow(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 50.dp)
            ) {
                items(items = episodes) { ep ->
                    Box(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(10.dp))
                            .width(210.dp)
                            .aspectRatio(16f / 9.5f)
                    ) {
                        PosterImage(
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
                    }
                }
            }
        }
    }
}