package com.nen.gatekeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nen.gatekeeper.data.model.VerifiedStreamData
import com.nen.gatekeeper.ui.theme.AppShapes
import com.nen.gatekeeper.ui.theme.AppSpacing
import com.nen.gatekeeper.ui.theme.StreamOrange
import com.nen.gatekeeper.ui.theme.StreamVerified

@Composable
fun RecommendedStreamCard(
    stream: VerifiedStreamData,
    modifier: Modifier = Modifier,
    onPlayClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShapes.card)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        StreamVerified.copy(alpha = 0.15f),
                        Color.Black.copy(alpha = 0.65f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = StreamVerified.copy(alpha = 0.40f),
                shape = AppShapes.card
            )
            .padding(AppSpacing.small)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(AppShapes.badge)
                        .background(StreamVerified)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.StarRate,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "RECOMMENDED",
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }

                if (stream.isSeekSupported && stream.isAlive) {
                    Spacer(modifier = Modifier.width(6.dp))
                    SeekBadge(isSeekSupported = true)
                }

                stream.verifiedSize?.let { sizeText ->
                    Spacer(modifier = Modifier.weight(1f))
                    SizeBadge(sizeText = sizeText)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stream.name,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = onPlayClick,
                    shape = AppShapes.inner,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StreamVerified,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Play Now",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun MirrorStreamCard(
    stream: VerifiedStreamData,
    modifier: Modifier = Modifier,
    onPlayClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShapes.inner)
            .background(Color.White.copy(alpha = 0.04f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.08f),
                shape = AppShapes.inner
            )
            .padding(10.dp)
    ) {
        Column {
            // Top Row: Seek Badge (Left) + Size Badge (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SeekBadge(isSeekSupported = stream.isSeekSupported)

                stream.verifiedSize?.let { sizeText ->
                    SizeBadge(sizeText = sizeText)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Row: Server Name (Left) + Play Button (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stream.name,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onPlayClick,
                    shape = AppShapes.badge,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StreamVerified.copy(alpha = 0.15f),
                        contentColor = StreamVerified
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play",
                        tint = StreamVerified,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Play",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StreamVerified
                    )
                }
            }
        }
    }
}

@Composable
private fun SeekBadge(
    isSeekSupported: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSeekSupported) StreamVerified.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f)
    val borderColor = if (isSeekSupported) StreamVerified.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.10f)
    val textColor = if (isSeekSupported) StreamVerified else Color.White.copy(alpha = 0.40f)

    Box(
        modifier = modifier
            .clip(AppShapes.badge)
            .background(bgColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = AppShapes.badge
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (isSeekSupported) {
                Icon(
                    imageVector = Icons.Filled.Bolt,
                    contentDescription = null,
                    tint = StreamOrange,
                    modifier = Modifier.size(11.dp)
                )
            }
            Text(
                text = if (isSeekSupported) "Seek" else "No Seek",
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SizeBadge(
    sizeText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(AppShapes.badge)
            .background(StreamOrange.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                color = StreamOrange.copy(alpha = 0.35f),
                shape = AppShapes.badge
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = sizeText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = StreamOrange,
            letterSpacing = 0.5.sp
        )
    }
}
