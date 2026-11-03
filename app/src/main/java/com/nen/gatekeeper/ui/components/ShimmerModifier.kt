package com.nen.gatekeeper.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.nen.gatekeeper.ui.theme.shimmerBase
import com.nen.gatekeeper.ui.theme.shimmerHighlight

// Shimmer placeholder effect for loading states
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition()
    val animateTransition = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Animate Shimmer"
    )
    val shimmerBase = MaterialTheme.colorScheme.shimmerBase
    val shimmerHighlight = MaterialTheme.colorScheme.shimmerHighlight

    val shimmerColors = remember(shimmerBase, shimmerHighlight) {
        listOf(shimmerBase, shimmerHighlight, shimmerBase)
    }
    this.drawBehind {
        val maxDim = maxOf(size.width, size.height)
        val progress = animateTransition.value

        val currentPos = (maxDim * 2f) * progress - maxDim

        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = currentPos, y = currentPos),
            end = Offset(x = currentPos + maxDim, y = currentPos + maxDim)
        )
        drawRect(
            brush = brush
        )
    }
}
