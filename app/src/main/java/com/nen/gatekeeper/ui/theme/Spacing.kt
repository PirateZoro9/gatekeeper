package com.nen.gatekeeper.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object AppSpacing {
    val extraSmall = 4.dp
    val small = 12.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp
    val cardGap = 8.dp
}

object AppSizes {
    val icon = 24.dp
    val posterWidth = 144.dp
}

object AppRadius {
    val badge = 4.dp
    val card = 8.dp
    val hero = 12.dp
}

object AppShapes {
    val badge = RoundedCornerShape(AppRadius.badge)
    val inner = RoundedCornerShape(AppRadius.card)
    val card = RoundedCornerShape(AppRadius.hero)
}
