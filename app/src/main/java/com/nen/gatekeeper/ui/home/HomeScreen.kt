package com.nen.gatekeeper.ui.home

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.nen.gatekeeper.data.model.HomeRow
import com.nen.gatekeeper.ui.components.PosterImage
import com.nen.gatekeeper.ui.components.shimmerEffect
import com.nen.gatekeeper.ui.theme.AppRadius
import com.nen.gatekeeper.ui.theme.AppSizes
import com.nen.gatekeeper.ui.theme.AppSpacing
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

/**
 * Main home screen of the app.
 *
 * Displays the top branding bar and observes the ViewModel state
 * to show shimmer loading placeholders or the loaded movie rows.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
    ) {
        when (uiState) {
            is HomeUiState.Error -> {
                ErrorState(
                    (uiState as HomeUiState.Error).message,
                    loadData = { viewModel.loadHome() })
            }

            HomeUiState.Loading -> {
                LoadingShimmer()
            }

            is HomeUiState.Success -> {
                HomeContent((uiState as HomeUiState.Success).rows, contentPadding = contentPadding)
            }

            is HomeUiState.Empty -> {
                EmptyState(
                    reload = { viewModel.loadHome() }
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    reload: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = AppSpacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No content found",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        OutlinedButton(
            onClick = {
                reload
            }
        ) {
            Text(
                text = "Retry"
            )
        }
    }
}

// Placeholder for error UI when loading fails
@Composable
fun ErrorState(message: String, loadData: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = AppSpacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        OutlinedButton(
            onClick = loadData
        ) {
            Text(
                text = "Retry"
            )
        }
    }
}

// Skeleton placeholder matching the hero carousel and movie cards
@Composable
fun LoadingShimmer() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.small)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 5f)
                .clip(shape = RoundedCornerShape(AppRadius.hero))
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .padding(horizontal = AppSpacing.small)
                .height(30.dp)
                .clip(shape = RoundedCornerShape(5.dp))
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .shimmerEffect()
        )
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.cardGap)
        ) {
            repeat(times = 2) {
                Box(
                    modifier = Modifier
                        .width(AppSizes.posterWidth)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(AppRadius.card))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .shimmerEffect()
                )
            }
        }
    }
}

// Main content displaying the top hero carousel and category rows
@Composable
fun HomeContent(rows: List<HomeRow>, contentPadding: PaddingValues) {
    // Pick the first few movies for the featured hero carousel
    val heroCards = rows.firstOrNull()?.rowCards?.take(5) ?: emptyList()
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2) % heroCards.size,
        pageCount = { Int.MAX_VALUE }
    )
    val currentPage = pagerState.currentPage
    val currentPageList = listOf(
        currentPage - 1.coerceAtLeast(0),
        currentPage,
        currentPage + 1.coerceAtMost(pagerState.currentPage - 1)
    )
    val visiblePages = currentPageList.distinct().map { page ->
        val pageOffset = (currentPage - page) + pagerState.currentPageOffsetFraction
        val visibility = (1f - pageOffset.absoluteValue).coerceIn(0f, 1f)

        HeroPageLayer(
            index = page % heroCards.size,
            visibility = visibility,
            offset = pageOffset
        )
    }.sortedBy { it.visibility }
    val lifecycleOwner = LocalLifecycleOwner.current
    // Auto-scroll the hero carousel every 7 seconds
    LaunchedEffect(heroCards.size) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                delay(7000)
                try {
                    if (!pagerState.isScrollInProgress) {
                        val nextPage = pagerState.currentPage + 1
                        pagerState.animateScrollToPage(nextPage, animationSpec = tween(1500))
                    }
                } catch (signal: CancellationException) {

                }
            }
        }
    }


    LazyColumn(
        contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding() + AppSpacing.small)
    ) {
        // Featured hero banner carousel at the top
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 5f)
                    .clip(shape = RoundedCornerShape(AppRadius.hero))
            ) {

                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = false,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = 0.01f
                        }
                ) { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 5f)
                            .clip(shape = RoundedCornerShape(AppRadius.hero))
                            .background(color = MaterialTheme.colorScheme.background)
                    )
                }

                visiblePages.forEach { layer ->
                    val posterUrl = heroCards[layer.index].posterUrl
                    key(layer.index) {
                        PosterImage(
                            posterUrl = posterUrl,
                            contentDescription = "Hero Poster",
                            modifier = Modifier.fillMaxSize(),
                            alpha = layer.visibility
                        )
                    }
                }
            }
        }

        // Category rows with horizontally scrollable movie cards
        items(items = rows) { row ->
            Text(
                modifier = Modifier.padding(
                    vertical = AppSpacing.small,
                    horizontal = AppSpacing.small
                ),
                text = row.rowTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            LazyRow(
                modifier = Modifier.padding(horizontal = AppSpacing.small),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.cardGap)
            ) {
                // Show up to 20 movie posters per row
                items(row.rowCards.take(20), key = { rowCard -> rowCard.detailedUrl }) { rowCard ->
                    Column() {
                        Box(
                            modifier = Modifier
                                .width(AppSizes.posterWidth)
                                .aspectRatio(2f / 3f)
                                .clip(RoundedCornerShape(AppRadius.card))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            PosterImage(
                                rowCard.posterUrl,
                                "Row Poster",
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                        Text(
                            modifier = Modifier
                                .width(AppSizes.posterWidth)
                                .padding(horizontal = 1.dp),
                            text = rowCard.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar() {
    Box(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.medium, vertical = 5.dp)
            .height(64.dp)
            .clip(shape = RoundedCornerShape(50))
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.02f)
                    )
                )
            )
            .border(
                width = 1.dp, brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.10f),
                        Color.Transparent
                    )
                ), shape = RoundedCornerShape(50)
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            IconButton(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                onClick = { /* Home */ }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(AppSizes.icon),
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Home",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Home",
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            IconButton(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                onClick = { /* Discover */ }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(AppSizes.icon),
                        imageVector = Icons.Filled.Explore,
                        contentDescription = "Discover",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Discover",
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            IconButton(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                onClick = { /* Search */ }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(AppSizes.icon),
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Search",
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            IconButton(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                onClick = { /* Settings */ }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(AppSizes.icon),
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Settings",
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private data class HeroPageLayer(val index: Int, val visibility: Float, val offset: Float)