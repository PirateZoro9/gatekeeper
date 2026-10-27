package com.nen.gatekeeper.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.nen.gatekeeper.R

@Composable
fun AppImage(
    posterUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alpha: Float = 1f
) {
    val request =
        ImageRequest.Builder(context = LocalContext.current).data(posterUrl).crossfade(true).build()
    AsyncImage(
        model = request,
        contentDescription = contentDescription,
        modifier = modifier,
        placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
        error = painterResource(R.drawable.no_poster),
        fallback = painterResource(R.drawable.no_poster),
        contentScale = ContentScale.Crop,
        alpha = alpha
    )
}