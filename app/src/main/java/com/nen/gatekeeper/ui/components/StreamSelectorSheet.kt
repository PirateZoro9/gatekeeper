package com.nen.gatekeeper.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nen.gatekeeper.data.model.StreamLink
import kotlinx.coroutines.launch

// Bottom sheet for selecting video quality and stream sources
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StreamSelectorSheet(
    title: String,
    streamLink: List<StreamLink>,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onVerifyClick: (List<String>) -> Unit
) {
    // Quality chip filter state
    var selectedQuality by rememberSaveable {
        mutableStateOf("All")
    }
    LaunchedEffect(streamLink) {
        selectedQuality = "All"
    }

    // Quality list sorted from 4K down to 480p
    val qualityList = remember(streamLink) {
        listOf("All") + streamLink.map { it.quality }.filter { it.isNotBlank() }.distinct()
            .sortedByDescending {
                qualityRank(it)
            }
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Edge fading animation for chips scroll
    val leftFadeColor by animateColorAsState(
        targetValue = if (listState.canScrollBackward) Color.Transparent else Color.Black,
        animationSpec = tween(300)
    )
    val rightFadeColor by animateColorAsState(
        targetValue = if (listState.canScrollForward) Color.Transparent else Color.Black.copy(),
        animationSpec = tween(300)
    )

    // Filtered stream links by selected quality
    val filteredLinks = remember(selectedQuality, streamLink) {
        if (selectedQuality == "All") {
            streamLink.sortedByDescending {
                qualityRank(it.quality)
            }
        } else {
            streamLink.filter {
                it.quality == selectedQuality
            }
        }
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Consumes remaining fling velocity to prevent sheet jumping
    val speedStopper = remember {
        object : NestedScrollConnection {
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                return available
            }
        }
    }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Smooth sheet dismissal with exit animation
    val closeSheet: () -> Unit = {
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss()
            }
        }
    }
    if (isOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = closeSheet,
            properties = ModalBottomSheetProperties(
                shouldDismissOnBackPress = true
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = screenHeight * 0.75f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 350,
                            easing = FastOutSlowInEasing
                        )
                    )
            ) {
                // Header with title and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Available Sources & Qualities",
                            style = TextStyle(
                                color = Color(0xFF00FF88),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                shadow = Shadow(
                                    color = Color(0xFF00FF88).copy(alpha = 0.50f),
                                    offset = Offset(0f, 0f),
                                    blurRadius = 12f
                                )
                            )
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = {
                            closeSheet()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = "Cancel",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (streamLink.isNotEmpty()) {
                    // Quality filter chips row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                    val brush = Brush.horizontalGradient(
                                        0.0f to leftFadeColor,
                                        0.08f to Color.Black,
                                        0.88f to Color.Black,
                                        1.00f to rightFadeColor
                                    )
                                    drawRect(
                                        brush = brush,
                                        blendMode = BlendMode.DstIn
                                    )
                                },
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(qualityList) { index, quality ->
                                val isSelected = selectedQuality == quality
                                val count = if (quality == "All") {
                                    streamLink.size
                                } else {
                                    streamLink.count {
                                        it.quality == quality
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
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
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            selectedQuality = quality
                                            scope.launch {
                                                listState.animateScrollToItem(index)
                                            }
                                        }
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
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = {
                                val urls = filteredLinks.map {
                                    it.url
                                }
                                onVerifyClick(urls)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.08f),
                                contentColor = Color(0xFF00FF88)
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = Color(0xFF00FF88).copy(alpha = 0.5f)
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = "⚡ Verify Seek",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    // Stream links list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .nestedScroll(speedStopper),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredLinks) { streamInfo ->
                            // Stream source card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.20f))
                                    .border(
                                        width = 1.dp,
                                        color = Color.White.copy(alpha = 0.10f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Column() {
                                    // Quality badge and file size
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color.White.copy(alpha = 0.10f))
                                                .border(
                                                    width = 1.dp,
                                                    color = Color.White.copy(alpha = 0.20f),
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 7.dp, vertical = 3.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (streamInfo.quality.contains(
                                                        "4k",
                                                        ignoreCase = true
                                                    ) || streamInfo.quality.contains("2160")
                                                ) "2160p / 4K" else streamInfo.quality,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                letterSpacing = 0.5.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        streamInfo.size?.let { sizeText ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFFFFA726).copy(alpha = 0.15f))
                                                    .border(
                                                        width = 1.dp,
                                                        color = Color(0xFFFFA726).copy(alpha = 0.35f),
                                                        shape = RoundedCornerShape(4.dp)
                                                    )
                                                    .padding(horizontal = 7.dp, vertical = 3.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = sizeText,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFFA726),
                                                    letterSpacing = 0.5.sp
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    // Server name and play action button
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White.copy(alpha = 0.08f))
                                            .border(
                                                width = 1.dp,
                                                color = Color.White.copy(alpha = 0.14f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                // Link clicked
                                            }
                                            .padding(horizontal = 12.dp, vertical = 9.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = streamInfo.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.PlayArrow,
                                                    contentDescription = "Play",
                                                    tint = Color(0xFF00FF88),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = "Play",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF00FF88)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Empty state fallback when no links found
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.06f))
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.12f),
                                    shape = CircleShape
                                )
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.5f)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No Streaming Links Available",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sources for this title have not been uploaded or verified yet.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// Rank higher resolution qualities first
fun qualityRank(q: String): Int = when {
    q.contains("4K", ignoreCase = true) || q.contains("2160") -> 4
    q.contains("1080") -> 3
    q.contains("720") -> 2
    q.contains("480") -> 1
    else -> 0
}