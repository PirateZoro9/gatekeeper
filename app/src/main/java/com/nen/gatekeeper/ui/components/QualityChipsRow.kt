package com.nen.gatekeeper.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nen.gatekeeper.data.model.StreamLink
import com.nen.gatekeeper.ui.theme.AppShapes
import com.nen.gatekeeper.ui.theme.AppSpacing
import com.nen.gatekeeper.ui.theme.StreamVerified
import kotlinx.coroutines.launch

// Horizontal quality filter chips with edge fading and verify seek button
@Composable
fun QualityChipsRow(
    qualityList: List<String>,
    selectedQuality: String,
    streamLinks: List<StreamLink>,
    onQualitySelect: (String) -> Unit,
    onVerifyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Edge fading animation for chips scroll
    val leftFadeColor by animateColorAsState(
        targetValue = if (listState.canScrollBackward) Color.Transparent else Color.Black,
        animationSpec = tween(300)
    )
    val rightFadeColor by animateColorAsState(
        targetValue = if (listState.canScrollForward) Color.Transparent else Color.Black,
        animationSpec = tween(300)
    )

    val qualityCounts = remember(streamLinks) {
        streamLinks.groupingBy { it.quality }.eachCount()
    }

    val fadeBrush = remember(leftFadeColor, rightFadeColor) {
        Brush.horizontalGradient(
            0.0f to leftFadeColor,
            0.08f to Color.Black,
            0.88f to Color.Black,
            1.00f to rightFadeColor
        )
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .graphicsLayer(
                    compositingStrategy = CompositingStrategy.Offscreen
                )
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = fadeBrush,
                        blendMode = BlendMode.DstIn
                    )
                },
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.cardGap)
        ) {
            itemsIndexed(qualityList) { index, quality ->
                val isSelected = selectedQuality == quality
                val count = if (quality == "All") {
                    streamLinks.size
                } else {
                    qualityCounts[quality] ?: 0
                }
                QualityChipItem(
                    quality = quality,
                    count = count,
                    isSelected = isSelected,
                    onClick = {
                        onQualitySelect(quality)
                        scope.launch {
                            listState.animateScrollToItem(index)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Button(
            onClick = onVerifyClick,
            shape = AppShapes.inner,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.08f),
                contentColor = StreamVerified
            ),
            border = BorderStroke(
                width = 1.dp,
                color = StreamVerified.copy(alpha = 0.5f)
            ),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
            modifier = Modifier.height(34.dp)
        ) {
            Text(
                text = "⚡ Verify & Play",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Single quality badge chip
@Composable
fun QualityChipItem(
    quality: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(AppShapes.inner)
            .background(
                color = if (isSelected) Color.White else Color.White.copy(
                    alpha = 0.08f
                )
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color.White else Color.White.copy(
                    alpha = 0.14f
                ),
                shape = AppShapes.inner
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$quality ($count)",
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.Black else Color.White
        )
    }
}
