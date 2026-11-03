package com.nen.gatekeeper.ui.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nen.gatekeeper.data.model.CardDetails
import com.nen.gatekeeper.data.model.HomeRow
import com.nen.gatekeeper.ui.components.AppImage
import com.nen.gatekeeper.ui.theme.AppShapes
import com.nen.gatekeeper.ui.theme.AppSizes
import com.nen.gatekeeper.ui.theme.AppSpacing

// Category row with title and horizontal cards
@Composable
fun HomeCategoryRow(
    row: HomeRow,
    onCardClick: (cardUrl: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(
                start = AppSpacing.medium,
                end = AppSpacing.medium,
                top = AppSpacing.cardGap,
                bottom = 6.dp
            ),
            text = row.rowTitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = AppSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.cardGap)
        ) {
            items(row.rowCards.take(20), key = { rowCard -> rowCard.detailedUrl }) { rowCard ->
                MovieCard(
                    card = rowCard,
                    onClick = { onCardClick(rowCard.detailedUrl) }
                )
            }
        }
    }
}

// Single movie poster card with metadata badges
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
                .clip(shape = AppShapes.inner)
                .clickable(onClick = onClick)
        ) {
            AppImage(
                posterUrl = card.posterUrl,
                contentDescription = "Row Poster",
                modifier = Modifier.fillMaxSize(),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
            ) {
                card.qualityTag?.let { MetaBadge(it) }
                Spacer(modifier = Modifier.weight(1f))
                card.rating?.let { MetaBadge(it) }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            modifier = Modifier
                .width(AppSizes.posterWidth)
                .padding(horizontal = 2.dp),
            text = card.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        card.releaseYear?.let { year ->
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = year,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }
    }
}

// Resolution, year, or rating metadata pill badge
@Composable
fun MetaBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(shape = AppShapes.badge)
            .background(Color.White.copy(alpha = 0.11f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.20f),
                shape = AppShapes.badge
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
