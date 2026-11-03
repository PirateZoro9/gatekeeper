package com.nen.gatekeeper.ui.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nen.gatekeeper.data.model.Episode
import com.nen.gatekeeper.data.model.Season
import com.nen.gatekeeper.ui.components.AppImage
import com.nen.gatekeeper.ui.theme.Accent
import com.nen.gatekeeper.ui.theme.AppShapes
import com.nen.gatekeeper.ui.theme.AppSpacing

// Series section with seasons and episodes
@Composable
fun DetailSeriesSection(
    seasons: List<Season>,
    modifier: Modifier = Modifier,
    posterUrl: String?,
    onEpisodeClick: (episode: Episode, seasonBadge: String) -> Unit
) {
    var selectedSeasonNumber by rememberSaveable(seasons) {
        mutableStateOf(
            seasons.firstOrNull()?.seasonNumber ?: 1
        )
    }
    val selectedSeason = seasons.find {
        it.seasonNumber == selectedSeasonNumber
    }
    val episodes = selectedSeason?.episodes.orEmpty()
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
                    .clip(shape = AppShapes.badge)
                    .background(Accent.copy(alpha = 0.15f))
                    .border(
                        width = 1.dp,
                        color = Accent.copy(alpha = 0.30f),
                        shape = AppShapes.badge
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${episodes.size} Episodes Available",
                    color = Accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Season selector chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.cardGap),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(items = seasons, key = { it.seasonNumber }) { season ->
                val isSelected = selectedSeason == season
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            color = if (isSelected) Color.White else Color.Black
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.15f),
                            shape = CircleShape
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
            // Animated episode cards carousel
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
                val seasonListState =
                    rememberSaveable(targetSeasonNumber, saver = LazyListState.Saver) {
                        LazyListState()
                    }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 50.dp),
                    state = seasonListState
                ) {
                    items(items = currentEpisodes, key = { it.episodeNumber }) { ep ->
                        EpisodeCard(
                            isSpecial = currentSeason?.isSpecial == true,
                            seasonNumber = currentSeason?.seasonNumber,
                            episode = ep,
                            posterUrl = posterUrl,
                            onEpisodeClick = onEpisodeClick
                        )
                    }
                }
            }
        }
    }
}

// Single episode card with blurred poster
@Composable
fun EpisodeCard(
    isSpecial: Boolean = false,
    seasonNumber: Int?,
    episode: Episode,
    posterUrl: String?,
    modifier: Modifier = Modifier,
    onEpisodeClick: (episode: Episode, seasonBadge: String) -> Unit
) {
    // Season and episode badge label
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
            .clickable {
                onEpisodeClick(
                    episode, seasonBadge
                )
            }
    ) {
        // Blurred poster background
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
                        .clip(AppShapes.badge)
                        .background(color = Color.White.copy(alpha = 0.05f))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.25f),
                            AppShapes.badge
                        )
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = seasonBadge,
                        fontSize = 12.sp,
                        color = Color.White
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
