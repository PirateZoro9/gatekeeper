package com.nen.gatekeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nen.gatekeeper.data.model.StreamLink
import com.nen.gatekeeper.ui.detail.StreamUIState

// Single stream source card with quality badge, size badge, and server name
@Composable
fun StreamCard(
    streamInfo: StreamLink,
    modifier: Modifier = Modifier,
    onPlayClick: () -> Unit = {},
    streamUIState: StreamUIState
) {
    val formattedQuality = if (streamInfo.quality.contains(
            "4k",
            ignoreCase = true
        ) || streamInfo.quality.contains("2160")
    ) "2160p / 4K" else streamInfo.quality
    Box(
        modifier = modifier
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
        Column {
            // Quality badge and file size row
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
                        text = formattedQuality,
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

            // Server name
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
                    .padding(horizontal = 12.dp, vertical = 9.dp)
            ) {
                Text(
                    text = streamInfo.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}
