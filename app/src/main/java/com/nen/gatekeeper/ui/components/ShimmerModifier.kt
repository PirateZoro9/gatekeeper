package com.nen.gatekeeper.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.nen.gatekeeper.ui.theme.shimmerBase
import com.nen.gatekeeper.ui.theme.shimmerHighlight

// Shimmer placeholder effect for loading states
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition()
    val animateTransition = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Animate Shimmer"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.shimmerBase,
            MaterialTheme.colorScheme.shimmerHighlight,
            MaterialTheme.colorScheme.shimmerBase,
        ),
        start = Offset(x = animateTransition.value - 500, y = animateTransition.value - 500),
        end = Offset(x = animateTransition.value, y = animateTransition.value)
    )

    this.background(brush)
}
