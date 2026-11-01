package com.nen.gatekeeper.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Floating back button top bar
@Composable
fun DetailTopBar(
    onBackClick: () -> Unit,
    contentPadding: PaddingValues
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = contentPadding.calculateTopPadding() + 4.dp,
                start = 14.dp,
                end = 14.dp
            )
    ) {
        OutlinedIconButton(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(50))
                .background(
                    color = Color.Black.copy(alpha = 0.40f)
                )
                .size(40.dp),
            onClick = onBackClick,
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}
