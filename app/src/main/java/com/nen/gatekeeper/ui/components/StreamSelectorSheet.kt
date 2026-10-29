package com.nen.gatekeeper.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nen.gatekeeper.data.model.StreamLink
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StreamSelectorSheet(
    title: String,
    streamLink: List<StreamLink>,
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    var selectedQuality by rememberSaveable {
        mutableStateOf("All")
    }
    val qualityList = remember(streamLink) {
        listOf("All") + streamLink.map {
            it.quality
        }.distinct()
    }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    if (isOpen) {
        ModalBottomSheet(
            onDismissRequest = onDismiss
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
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
                        onClick = onDismiss
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = "Cancel",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

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
                                    0.0f to if (listState.canScrollBackward) Color.Transparent else Color.Black,
                                    0.08f to Color.Black,
                                    0.88f to Color.Black,
                                    1.00f to if (listState.canScrollForward) Color.Transparent else Color.Black
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
                                    text = quality,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {},
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
            }
        }
    }
}
