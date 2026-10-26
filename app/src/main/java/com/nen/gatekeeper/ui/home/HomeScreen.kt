package com.nen.gatekeeper.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.nen.gatekeeper.data.model.CardDetails
import com.nen.gatekeeper.data.model.HomeRow
import com.nen.gatekeeper.ui.components.EmptyState
import com.nen.gatekeeper.ui.components.ErrorState
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
    contentPadding: PaddingValues,
    onCardClick: (cardUrl: String) -> Unit
) {
    // uiState is collected here, when the state changes the screen updates itself
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = modifier
    ) {
        Crossfade(targetState = uiState, animationSpec = tween(500)) { state ->
            when (state) {
                is HomeUiState.Error -> {
                    ErrorState(message = state.message, onRetry = { viewModel.loadHome() })
                }

                is HomeUiState.Loading -> {
                    HomeShimmer()
                }

                is HomeUiState.Success -> {
                    HomeContent(
                        rows = state.rows,
                        contentPadding = contentPadding,
                        onCardClick = onCardClick
                    )
                }

                is HomeUiState.Empty -> {
                    EmptyState()
                }
            }
        }
    }
}

// Skeleton placeholder matching the hero carousel and movie cards
@Composable
fun HomeShimmer() {
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
fun HomeContent(
    rows: List<HomeRow>,
    contentPadding: PaddingValues,
    onCardClick: (cardUrl: String) -> Unit
) {
    // Pick the first few movies for the featured hero carousel
    val heroCards = rows.firstOrNull()?.rowCards?.take(5) ?: emptyList()
    val listState = rememberLazyListState()
    val isScrolling = listState.isScrollInProgress
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 100.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
        ) {
            // Featured hero banner carousel at the top
            item(contentType = "Hero") {
                HeroCarousel(
                    heroCards = heroCards,
                    onCardClick = onCardClick
                )
            }

            // Category rows with horizontally scrollable movie cards
            items(items = rows, contentType = { "Rows" }, key = { it.rowTitle }) { row ->
                HomeCategoryRow(
                    row = row,
                    onCardClick = onCardClick
                )
            }
        }
        HomeBottomBar(isScrolling = isScrolling, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

// Featured hero banner carousel with parallax effect, auto-scroll and animated indicators
@Composable
fun HeroCarousel(
    heroCards: List<CardDetails>,
    onCardClick: (cardUrl: String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (heroCards.isEmpty()) return

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

    val bg = MaterialTheme.colorScheme.background
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4f / 5f)
            .clip(shape = RoundedCornerShape(AppRadius.hero))
        // black fade on the bottom 40%, the poster melts into the background
    ) {
        // the real pager: the user can swipe the pages himself
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = true,
            modifier = Modifier.fillMaxSize(),
            // 1 page on both sides stays ready, so its poster is loaded before the swipe reaches it
        ) { page ->
            val hero = heroCards[page % heroCards.size]
            val hasMeta = hero.releaseYear != null || hero.rating != null || hero.qualityTag != null
            // how far this page is from the settled page (0 = settled, near -1 or 1 = one page away)
            val pageOffset =
                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            // the pager slides the page, this moves it back by the same amount, so the page stays in one place and only fades
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = size.width * pageOffset
                        alpha = (1f - pageOffset.absoluteValue).coerceIn(0f, 1f)
                    }
            ) {

                // the parallax (0.055) and scale go in through this modifier, PosterImage passes it to the image inside
                PosterImage(
                    posterUrl = hero.posterUrl,
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
                        text = hero.title,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (hasMeta) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                8.dp,
                                Alignment.CenterHorizontally
                            )
                        ) {
                            hero.releaseYear?.let {
                                MetaBadge(it)
                            }
                            hero.rating?.let {
                                MetaBadge(it)

                            }
                            hero.qualityTag?.let {
                                MetaBadge(it)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onCardClick(hero.detailedUrl) },
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

// Category row with horizontally scrollable movie cards
@Composable
fun HomeCategoryRow(
    row: HomeRow,
    onCardClick: (cardUrl: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
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
                MovieCard(
                    card = rowCard,
                    onClick = { onCardClick(rowCard.detailedUrl) }
                )
            }
        }
    }
}

// Reusable single movie card component
@Composable
fun MovieCard(
    card: CardDetails,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .width(AppSizes.posterWidth)
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(AppRadius.card))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable(onClick = onClick)
        ) {
            PosterImage(
                posterUrl = card.posterUrl,
                contentDescription = "Row Poster",
                modifier = Modifier.fillMaxSize(),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
            ) {
                card.qualityTag?.let {
                    MetaBadge(it)
                }
                Spacer(modifier = Modifier.weight(1f))
                card.rating?.let {
                    MetaBadge(it)
                }
            }
        }
        Text(
            modifier = Modifier
                .width(AppSizes.posterWidth)
                .padding(horizontal = 1.dp),
            text = card.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        card.releaseYear?.let { year ->
            Text(
                text = year,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 1.dp)
            )
        }
    }
}

@Composable
// the bottom bar is only a look for now, its screens will be made later
fun HomeBottomBar(
    isScrolling: Boolean,
    modifier: Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isScrolling) 0.85f else 1f,
        label = "bottomBarScale"
    )
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .navigationBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.medium, vertical = 5.dp)
            .height(64.dp)
            .clip(shape = RoundedCornerShape(50))
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.10f),
                        Color.Black.copy(alpha = 0.40f)
                    )
                )
            )
            .border(
                width = 1.dp, brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.20f),
                        Color.White.copy(alpha = 0.05f),
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
            BottomBarItem(
                icon = Icons.Filled.Home,
                label = "Home",
                onClick = { /* Home */ },
                modifier = Modifier.weight(1f)
            )
            BottomBarItem(
                icon = Icons.Filled.Explore,
                label = "Discover",
                onClick = { /* Discover */ },
                modifier = Modifier.weight(1f)
            )
            BottomBarItem(
                icon = Icons.Filled.Search,
                label = "Search",
                onClick = { /* Search */ },
                modifier = Modifier.weight(1f)
            )
            BottomBarItem(
                icon = Icons.Filled.Settings,
                label = "Settings",
                onClick = { /* Settings */ },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Reusable navigation bar item
@Composable
private fun BottomBarItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier.height(56.dp),
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(AppSizes.icon),
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun MetaBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White.copy(alpha = 0.11f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.20f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            letterSpacing = 0.5.sp
        )
    }
}