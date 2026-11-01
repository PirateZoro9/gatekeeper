package com.nen.gatekeeper.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.nen.gatekeeper.ui.theme.AppSizes
import com.nen.gatekeeper.ui.theme.AppSpacing

// Floating bottom navigation bar with scrolling scale animation
@Composable
fun HomeBottomBar(
    isScrolling: Boolean,
    modifier: Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isScrolling) 0.85f else 1f,
        label = "bottomBarScale"
    )
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .navigationBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.medium, vertical = 5.dp)
            .height(64.dp)
            .clip(shape = RoundedCornerShape(50))
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.10f),
                        Color.Black.copy(alpha = 0.40f)
                    )
                )
            )
            .border(
                width = 1.dp, brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.20f),
                        Color.White.copy(alpha = 0.05f),
                        Color.Transparent
                    )
                ), shape = RoundedCornerShape(50)
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            BottomBarItem(
                icon = Icons.Filled.Home,
                label = "Home",
                onClick = { /* Home */ },
                modifier = Modifier.weight(1f)
            )
            BottomBarItem(
                icon = Icons.Filled.Explore,
                label = "Discover",
                onClick = { /* Discover */ },
                modifier = Modifier.weight(1f)
            )
            BottomBarItem(
                icon = Icons.Filled.Search,
                label = "Search",
                onClick = { /* Search */ },
                modifier = Modifier.weight(1f)
            )
            BottomBarItem(
                icon = Icons.Filled.Settings,
                label = "Settings",
                onClick = { /* Settings */ },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Single navigation icon and label button
@Composable
private fun BottomBarItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier.height(56.dp),
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(AppSizes.icon),
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
