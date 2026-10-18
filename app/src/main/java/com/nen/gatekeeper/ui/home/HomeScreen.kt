package com.nen.gatekeeper.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.AsyncImage
import com.nen.gatekeeper.data.model.HomeRow
import com.nen.gatekeeper.ui.components.shimmerEffect
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
fun HomeScreen(viewModel: HomeViewModel, modifier: Modifier = Modifier, onSearchClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = modifier,
    ) {
        TopBar(onSearchClick = onSearchClick)
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
                HomeContent((uiState as HomeUiState.Success).rows)
            }

            is HomeUiState.Empty -> {
                EmptyState(
                    reload = { viewModel.loadHome() }
                )
            }
        }
        BottomBar()
    }
}

// App header with stylized GATEKEEPER title and search button
@Composable
fun TopBar(
    modifier: Modifier = Modifier, onSearchClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append("GATE")
                }
                append(" ")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                    append("KEEPER")
                }
            },
            style = MaterialTheme.typography.titleMedium,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        OutlinedIconButton(
            onClick = onSearchClick,
            colors = IconButtonDefaults.outlinedIconButtonColors(
                containerColor = Color.White.copy(alpha = 0.05f)
            ),
            border = BorderStroke(width = 1.dp, color = Color.White.copy(alpha = 0.08f))
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search"
            )
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
            .padding(vertical = 10.dp),
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
            .padding(vertical = 10.dp),
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
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .aspectRatio(4f / 5f)
                .clip(shape = RoundedCornerShape(7.dp))
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .padding(horizontal = 10.dp)
                .height(30.dp)
                .clip(shape = RoundedCornerShape(5.dp))
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .shimmerEffect()
        )
        Row(
            modifier = Modifier.padding(horizontal = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(times = 2) {
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .shimmerEffect()
                )
            }
        }
    }
}

// Main content displaying the top hero carousel and category rows
@Composable
fun HomeContent(rows: List<HomeRow>) {
    // Pick the first few movies for the featured hero carousel
    val heroCards = rows.firstOrNull()?.rowCards?.take(5) ?: emptyList()
    val latestHeroCards by rememberUpdatedState(heroCards)
    val pagerState = rememberPagerState(pageCount = { latestHeroCards.size })
    val lifecycleOwner = LocalLifecycleOwner.current
    // Auto-scroll the hero carousel every 7 seconds
    LaunchedEffect(pagerState) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                delay(7000)
                try {
                    if (!pagerState.isScrollInProgress) {
                        val nextPage = pagerState.currentPage + 1
                        if (nextPage < latestHeroCards.size) {
                            pagerState.animateScrollToPage(nextPage)
                        } else {
                            pagerState.animateScrollToPage(0)
                        }
                    }
                } catch (signal: CancellationException) {

                }
            }
        }
    }


    LazyColumn(
    ) {
        // Featured hero banner carousel at the top
        item {
            HorizontalPager(
                state = pagerState
            ) { page ->
                // Slow zoom-in animation when a slide settles
                val zoom = remember { Animatable(1f) }
                LaunchedEffect(pagerState.settledPage) {
                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        if (page == pagerState.settledPage) {
                            zoom.snapTo(1f)
                            zoom.animateTo(1.10f, animationSpec = tween(7000))
                        }
                    }
                }
                val card = heroCards[page]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .aspectRatio(4f / 5f)
                        .clip(shape = RoundedCornerShape(7.dp))
                        .background(color = MaterialTheme.colorScheme.background)
                        // Scale and fade cards slightly as they swipe away
                        .graphicsLayer {
                            val pageOffset =
                                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                            val distance = pageOffset.absoluteValue.coerceIn(0f, 1f)
                            scaleX = 1f - (distance * 0.4f)
                            scaleY = scaleX
                            alpha = 1f - (distance * 0.7f)
                        }
                ) {
                    Text(
                        text = card.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.align(alignment = Alignment.Center)
                    )
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = zoom.value
                                scaleY = zoom.value
                            },
                        model = card.posterUrl,
                        contentDescription = "Hero Poster",
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }

        // Category rows with horizontally scrollable movie cards
        items(items = rows) { row ->
            Text(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                text = row.rowTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            LazyRow(
                modifier = Modifier.padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Show up to 20 movie posters per row
                items(row.rowCards.take(20), key = { rowCard -> rowCard.detailedUrl }) { rowCard ->
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .aspectRatio(2f / 3f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Text(
                            text = rowCard.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.align(alignment = Alignment.Center)
                        )
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = rowCard.posterUrl,
                            contentDescription = "Row Cards",
                            contentScale = ContentScale.Crop
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
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(70.dp)
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
                        modifier = Modifier.size(24.dp),
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
                        modifier = Modifier.size(24.dp),
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
                        modifier = Modifier.size(24.dp),
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
                        modifier = Modifier.size(24.dp),
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