package com.nen.gatekeeper.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nen.gatekeeper.ui.components.shimmerEffect
import com.nen.gatekeeper.ui.theme.AppRadius
import com.nen.gatekeeper.ui.theme.AppSpacing

// Shimmer loading placeholder
@Composable
fun DetailShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .clip(shape = RoundedCornerShape(AppRadius.hero))
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(AppSpacing.medium))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(48.dp)
                    .clip(shape = RoundedCornerShape(24.dp))
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                    .shimmerEffect()
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.large))

        Column(
            modifier = Modifier.padding(horizontal = AppSpacing.medium)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(20.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(14.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(AppSpacing.small))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(14.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                    .shimmerEffect()
            )
        }
    }
}
