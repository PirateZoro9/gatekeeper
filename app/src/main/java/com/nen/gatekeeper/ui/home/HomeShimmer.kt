package com.nen.gatekeeper.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nen.gatekeeper.ui.components.shimmerEffect
import com.nen.gatekeeper.ui.theme.AppRadius
import com.nen.gatekeeper.ui.theme.AppSizes
import com.nen.gatekeeper.ui.theme.AppSpacing

// Shimmer placeholder
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
                .padding(horizontal = AppSpacing.medium)
                .height(30.dp)
                .clip(shape = RoundedCornerShape(5.dp))
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .shimmerEffect()
        )
        Row(
            modifier = Modifier.padding(horizontal = AppSpacing.medium),
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
