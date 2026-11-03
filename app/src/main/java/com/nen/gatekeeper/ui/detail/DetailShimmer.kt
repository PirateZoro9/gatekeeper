package com.nen.gatekeeper.ui.detail

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nen.gatekeeper.ui.components.shimmerEffect
import com.nen.gatekeeper.ui.theme.AppShapes
import com.nen.gatekeeper.ui.theme.AppSpacing

// Shimmer loading placeholder matching DetailHeroHeader and DetailContent layout to prevent Crossfade layout shift
@Composable
fun DetailShimmer(modifier: Modifier = Modifier) {
    val bg = MaterialTheme.colorScheme.background

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Hero Header Placeholder (matching DetailHeroHeader)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .clip(shape = AppShapes.card)
                .shimmerEffect()
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
                            size = Size(size.width, size.height * 0.4f)
                        )
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppSpacing.medium, vertical = AppSpacing.medium),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(28.dp)
                        .clip(shape = AppShapes.badge)
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Rating badge placeholder
                Box(
                    modifier = Modifier
                        .width(54.dp)
                        .height(20.dp)
                        .clip(shape = AppShapes.badge)
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Play button placeholder (matches DetailHeroHeader width(100.dp))
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp)
                        .clip(shape = CircleShape)
                        .shimmerEffect()
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.cardGap))

        // Genre Chips Placeholder (Row of centered chips matching DetailGenreChips)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(
                AppSpacing.cardGap,
                Alignment.CenterHorizontally
            )
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .height(24.dp)
                        .clip(shape = CircleShape)
                        .shimmerEffect()
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.medium))

        // Synopsis Placeholder (Matching DetailSynopsisSection)
        Column(
            modifier = Modifier.padding(horizontal = AppSpacing.medium)
        ) {
            // Line 1
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(14.dp)
                    .clip(shape = AppShapes.badge)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(AppSpacing.cardGap))

            // Line 2
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.80f)
                    .height(14.dp)
                    .clip(shape = AppShapes.badge)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(AppSpacing.cardGap))

            // Line 3
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.50f)
                    .height(14.dp)
                    .clip(shape = AppShapes.badge)
                    .shimmerEffect()
            )
        }
    }
}
