package com.nen.gatekeeper.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Dark theme color scheme
private val GateKeeperColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = TextPrimary,
    primaryContainer = AccentPressed,
    onPrimaryContainer = TextPrimary,
    inversePrimary = Accent,

    secondary = Surface2,
    onSecondary = TextPrimary,
    secondaryContainer = Surface3,
    onSecondaryContainer = TextSecondary,

    tertiary = TextSecondary,
    onTertiary = Background,

    background = Background,
    onBackground = TextPrimary,

    surface = Surface1,
    onSurface = TextPrimary,
    surfaceVariant = Surface2,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = Surface1,
    surfaceContainerHigh = Surface2,
    surfaceContainerHighest = Surface3,

    outline = Divider,
    outlineVariant = Surface3
)

@Composable
fun GateKeeperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> GateKeeperColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Shimmer color extensions
val ColorScheme.shimmerBase: Color
    get() = ShimmerBase

val ColorScheme.shimmerHighlight: Color
    get() = ShimmerHighlight
