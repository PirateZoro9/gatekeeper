package com.nen.gatekeeper.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nen.gatekeeper.data.model.HomeRow
import com.nen.gatekeeper.ui.components.EmptyState
import com.nen.gatekeeper.ui.components.ErrorState
import com.nen.gatekeeper.ui.theme.AppSpacing

// Main home screen coordinator
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    onCardClick: (cardUrl: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
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

// Home content list
@Composable
fun HomeContent(
    rows: List<HomeRow>,
    contentPadding: PaddingValues,
    onCardClick: (cardUrl: String) -> Unit
) {
    val heroCards = remember(rows) {
        rows.firstOrNull()?.rowCards?.take(5) ?: emptyList()
    }
    val listState = rememberLazyListState()
    val isScrolling = listState.isScrollInProgress
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp + contentPadding.calculateBottomPadding()),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
        ) {
            // Hero banner
            item(key = "hero_carousel", contentType = "Hero") {
                HeroCarousel(
                    heroCards = heroCards,
                    onCardClick = onCardClick
                )
            }

            // Category rows
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
