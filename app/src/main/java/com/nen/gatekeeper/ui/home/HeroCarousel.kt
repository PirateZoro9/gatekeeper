package com.nen.gatekeeper.ui.home

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.repeatOnLifecycle
import com.nen.gatekeeper.data.model.CardDetails
import com.nen.gatekeeper.ui.components.AppImage
import com.nen.gatekeeper.ui.theme.AppShapes
import com.nen.gatekeeper.ui.theme.AppSpacing
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlin.math.absoluteValue

// Featured hero carousel with auto-scroll and indicators
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
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(heroCards) {
        if (heroCards.size > 1) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    delay(7000)
                    try {
                        if (!pagerState.isScrollInProgress) {
                            val nextPage = pagerState.currentPage + 1
                            pagerState.animateScrollToPage(nextPage, animationSpec = tween(1500))
                        }
                    } catch (signal: CancellationException) {
                        currentCoroutineContext().ensureActive()
                    }
                }
            }
        }
    }

    val bg = MaterialTheme.colorScheme.background
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4f / 5f)
            .clip(shape = AppShapes.card)
    ) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = true,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val hero = heroCards[page % heroCards.size]
            val hasMeta = hero.releaseYear != null || hero.rating != null || hero.qualityTag != null

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        val pageOffset =
                            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                        translationX = size.width * pageOffset
                        alpha = (1f - pageOffset.absoluteValue).coerceIn(0f, 1f)
                    }
            ) {
                AppImage(
                    posterUrl = hero.posterUrl,
                    contentDescription = "Hero Poster",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val pageOffset =
                                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
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
                        .padding(horizontal = AppSpacing.large, vertical = 44.dp),
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
                        Spacer(modifier = Modifier.height(AppSpacing.cardGap))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                AppSpacing.cardGap,
                                Alignment.CenterHorizontally
                            )
                        ) {
                            hero.releaseYear?.let { MetaBadge(it) }
                            hero.rating?.let { MetaBadge(it) }
                            hero.qualityTag?.let { MetaBadge(it) }
                        }
                    }
                    Spacer(modifier = Modifier.height(AppSpacing.small))
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
        if (heroCards.size > 1) {
            CarouselIndicators(
                heroCardsCount = heroCards.size,
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = AppSpacing.medium)
            )
        }
    }
}

@Composable
private fun CarouselIndicators(
    heroCardsCount: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val currentPage = pagerState.currentPage
    val currentPageList = listOf(
        (currentPage - 1).coerceAtLeast(0),
        currentPage,
        (currentPage + 1).coerceAtMost(pagerState.pageCount - 1)
    )
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.cardGap)
    ) {
        for (index in 0 until heroCardsCount) {
            val page = currentPageList.firstOrNull { it % heroCardsCount == index }
            val fraction = if (page == null) {
                0f
            } else {
                val pageOffset = (currentPage - page) + pagerState.currentPageOffsetFraction
                (1f - pageOffset.absoluteValue).coerceIn(0f, 1f)
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.onBackground
                            .copy(alpha = 0.35f + 0.57f * fraction)
                    )
                    .width(8.dp + 24.dp * fraction)
                    .height(8.dp)
            )
        }
    }
}