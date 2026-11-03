package com.nen.gatekeeper.ui.home

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
import com.nen.gatekeeper.ui.theme.AppSizes
import com.nen.gatekeeper.ui.theme.AppSpacing

@Composable
fun HomeShimmer(modifier: Modifier = Modifier) {
    val bg = MaterialTheme.colorScheme.background

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 5f)
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
            // Hero Title, Badges, and Button
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppSpacing.large, vertical = 44.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hero Title line
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(28.dp)
                        .clip(shape = AppShapes.badge)
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(AppSpacing.cardGap))

                // Metadata Badges placeholder
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        AppSpacing.cardGap,
                        Alignment.CenterHorizontally
                    )
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(18.dp)
                                .clip(shape = AppShapes.badge)
                                .shimmerEffect()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.small))

                // "View Details" Button placeholder
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(40.dp)
                        .clip(shape = CircleShape)
                        .shimmerEffect()
                )
            }

            // Bottom Carousel Indicator dots
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = AppSpacing.medium),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.cardGap)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .width(32.dp)
                        .height(8.dp)
                        .shimmerEffect()
                )
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .width(8.dp)
                            .height(8.dp)
                            .shimmerEffect()
                    )
                }
            }
        }

        Column {
            // Row Title
            Box(
                modifier = Modifier
                    .padding(
                        start = AppSpacing.medium,
                        end = AppSpacing.medium,
                        top = AppSpacing.cardGap,
                        bottom = 6.dp
                    )
                    .fillMaxWidth(0.35f)
                    .height(20.dp)
                    .clip(shape = AppShapes.badge)
                    .shimmerEffect()
            )

            // Movie Cards
            Row(
                modifier = Modifier.padding(horizontal = AppSpacing.medium),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.cardGap)
            ) {
                repeat(2) {
                    Column {
                        Box(
                            modifier = Modifier
                                .width(AppSizes.posterWidth)
                                .aspectRatio(2f / 3f)
                                .clip(shape = AppShapes.inner)
                                .shimmerEffect()
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Card Title line
                        Box(
                            modifier = Modifier
                                .width(AppSizes.posterWidth * 0.75f)
                                .height(14.dp)
                                .clip(shape = AppShapes.badge)
                                .shimmerEffect()
                        )
                    }
                }
            }
        }
    }
}
