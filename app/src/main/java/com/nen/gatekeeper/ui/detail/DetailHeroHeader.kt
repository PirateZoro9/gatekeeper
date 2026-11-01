package com.nen.gatekeeper.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nen.gatekeeper.ui.components.AppImage
import com.nen.gatekeeper.ui.theme.AppRadius

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
