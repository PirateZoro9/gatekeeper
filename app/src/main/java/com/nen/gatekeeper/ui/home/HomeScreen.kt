package com.nen.gatekeeper.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    // uiState is collected here, when the state changes the screen updates itself
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
    ) {
        Crossfade(targetState = uiState, animationSpec = tween(500)) { state ->
            when (state) {
                is HomeUiState.Error -> {
                    ErrorState(
                        state.message,
                        loadData = { viewModel.loadHome() })
                }

                is HomeUiState.Loading -> {
                    LoadingShimmer()
                }

                is HomeUiState.Success -> {
                    HomeContent(
                        state.rows,
                        contentPadding = contentPadding
                    )
                }

                is HomeUiState.Empty -> {
                    EmptyState(
                        reload = { viewModel.loadHome() }
                    )
                }
            }
        }
    }
}

@Composable
// when data loaded but not a single content was found, this is shown
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
            onClick = reload
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
    // all hero layers are built from these pager numbers
    val currentPage = pagerState.currentPage
    // only 3 pages are needed: previous, current and next
    val currentPageList = listOf(
        (currentPage - 1).coerceAtLeast(0),
        currentPage,
        (currentPage + 1).coerceAtMost(pagerState.pageCount - 1)
    )
    // auto scroll only runs while the screen is in front, so it is tied to the lifecycle
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
        item(contentType = "Hero") {
            val bg = MaterialTheme.colorScheme.background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 5f)
                    .clip(shape = RoundedCornerShape(AppRadius.hero))
                // black fade on the bottom 40%, the poster melts into the background
            ) {
                // the real pager: the user can swipe the pages himself
                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = true,
                    modifier = Modifier
                        .fillMaxSize(),
                    // 1 page on both sides stays ready, so its poster is loaded before the swipe reaches it
                    beyondViewportPageCount = 1
                ) { page ->
                    // how far this page is from the settled page (0 = settled, near -1 or 1 = one page away)
                    val pageOffset =
                        (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    // the pager slides the page, this moves it back by the same amount, so the page stays in one place and only fades
                    Box(
                        modifier = Modifier.graphicsLayer {
                            translationX = size.width * pageOffset
                            alpha = (1f - pageOffset.absoluteValue).coerceIn(0f, 1f)
                        }
                    ) {
                        // the parallax (0.055) and scale go in through this modifier, PosterImage passes it to the image inside
                        PosterImage(
                            posterUrl = heroCards[page % heroCards.size].posterUrl,
                            contentDescription = "Hero Poster",
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    translationX = -pageOffset * size.width * 0.055f
                                    scaleX = 1.14f
                                    scaleY = scaleX
                                }
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
                                    // first the content is drawn, then this fade rect on top of it
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
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp, vertical = 44.dp),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = heroCards[page % heroCards.size].title,
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { /* nothing yet */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.onBackground,
                                    contentColor = MaterialTheme.colorScheme.background
                                )
                            ) {
                                Text(text = "View Details")
                            }
                        }

                    }
                }

                // indicators: one pill for each film, this is outside the layer loop so the pills do not dissolve
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    heroCards.indices.forEach { index ->

                        val page = currentPageList.firstOrNull {
                            it % heroCards.size == index
                        }
                        val fraction = if (page == null) {
                            0f
                        } else {
                            val pageOffset =
                                (currentPage - page) + pagerState.currentPageOffsetFraction
                            (1f - pageOffset.absoluteValue).coerceIn(0f, 1f)
                        }
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.onBackground
                                        .copy(alpha = 0.35f + 0.57f * fraction)
                                )
                                // this is a formula, not if/else: when visibility grows the pill grows with it
                                .width(8.dp + 24.dp * fraction)
                                .height(8.dp)
                        )
                    }
                }
            }
        }

        // Category rows with horizontally scrollable movie cards
        items(items = rows, contentType = { "Rows" }) { row ->
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
// the bottom bar is only a look for now, its screens will be made later
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