package com.nen.gatekeeper.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
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
import com.nen.gatekeeper.ui.detail.StreamUIState
import kotlinx.coroutines.launch

// Main modal bottom sheet for stream source and quality selection
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StreamSelectorSheet(
    title: String,
    streamLink: List<StreamLink>,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onVerifyClick: (List<String>) -> Unit,
    streamUIState: StreamUIState
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
    val scope = rememberCoroutineScope()

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

    var isMirrorsExpanded by remember {
        mutableStateOf(false)
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
                StreamSheetHeader(
                    title = title,
                    onCloseClick = closeSheet,
                    streamUIState = streamUIState
                )
                Spacer(modifier = Modifier.height(14.dp))

                if (streamLink.isNotEmpty()) {
                    // Quality filter chips row with verify seek button
                    QualityChipsRow(
                        qualityList = qualityList,
                        selectedQuality = selectedQuality,
                        streamLinks = streamLink,
                        onQualitySelect = { selectedQuality = it },
                        onVerifyClick = {
                            val urls = streamLink.map { it.url }
                            onVerifyClick(urls)
                        }
                    )

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
                        if (streamUIState is StreamUIState.Success) {
                            val streams = streamUIState.streams
                            val recommendedStream = streams.firstOrNull {
                                it.isSeekSupported && it.isAlive
                            }
                            val otherStreams = if (recommendedStream != null) {
                                streams.filter {
                                    it.url != recommendedStream.url
                                }
                            } else {
                                streams
                            }
                            recommendedStream?.let {
                                item(key = it.url) {
                                    RecommendedStreamCard(
                                        stream = it,
                                        onPlayClick = {}
                                    )
                                }
                            }

                            items(items = otherStreams.take(2), key = { it.url }) {
                                MirrorStreamCard(
                                    stream = it,
                                    onPlayClick = {}
                                )
                            }

                            if (otherStreams.size > 2) {
                                val remainingCount = otherStreams.size - 2
                                if (isMirrorsExpanded) {
                                    item {
                                        Text(
                                            text = "BACKUP MIRRORS ($remainingCount)",
                                            style = TextStyle(
                                                color = Color.White.copy(alpha = 0.40f),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            ),
                                            modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                                        )
                                    }
                                    items(items = otherStreams.drop(2), key = { it.url }) {
                                        MirrorStreamCard(
                                            stream = it,
                                            onPlayClick = {}
                                        )
                                    }
                                }
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White.copy(alpha = 0.05f))
                                            .border(
                                                width = 1.dp,
                                                color = Color.White.copy(alpha = 0.10f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                isMirrorsExpanded = !isMirrorsExpanded
                                            }
                                            .padding(vertical = 9.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isMirrorsExpanded) {
                                                "▲  Show Less Mirrors"
                                            } else {
                                                "▼  Show $remainingCount More Mirrors"
                                            },
                                            style = TextStyle(
                                                color = Color(0xFFCCCCCC),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                letterSpacing = 0.3.sp
                                            )
                                        )
                                    }
                                }
                            }
                        } else {
                            items(items = filteredLinks, key = { it.url }) { streamInfo ->
                                StreamCard(
                                    streamInfo = streamInfo,
                                    streamUIState = streamUIState
                                )
                            }
                        }
                    }
                } else {
                    // Empty state fallback when no links found
                    StreamEmptyState()
                }
            }
        }
    }
}

// Sheet header with glowing title and cancel icon button
@Composable
fun StreamSheetHeader(
    title: String,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    streamUIState: StreamUIState
) {
    Column() {
        Row(
            modifier = modifier.fillMaxWidth(),
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
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Filled.Cancel,
                    contentDescription = "Cancel",
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        }
        var loadingState by remember {
            mutableStateOf<StreamUIState.Loading?>(null)
        }

        if (streamUIState is StreamUIState.Loading) {
            loadingState = streamUIState
        }
        AnimatedVisibility(
            visible = streamUIState is StreamUIState.Loading,
            enter = fadeIn(tween(300)) + expandVertically(tween(300)),
            exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
        ) {
            val loading = loadingState
            if (loading != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = loading.statusText,
                            color = Color(0xFF00FF88),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.2.sp
                        )
                        if (loading.streamCountText.isNotBlank()) {
                            Text(
                                text = loading.streamCountText,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { loading.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = Color(0xFF00FF88),
                        trackColor = Color.White.copy(alpha = 0.1f),
                        drawStopIndicator = {},
                        gapSize = 0.dp
                    )
                }
            }
        }
    }
}

// Fallback view when no stream links are available
@Composable
fun StreamEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
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
