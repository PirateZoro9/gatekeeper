package com.nen.gatekeeper.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.decode.DataSource
import coil.request.ImageRequest
import com.nen.gatekeeper.R
import kotlinx.coroutines.launch

@Composable
fun PosterImage(
    posterUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    settle: Boolean = false
) {
    val request = ImageRequest.Builder(context = LocalContext.current).data(posterUrl).build()
    val scope = rememberCoroutineScope()
    val fadedAlpha = remember {
        Animatable(0f)
    }
    val settleScale = remember {
        Animatable(if (settle) 1.04f else 1f)
    }
    AsyncImage(
        model = request,
        contentDescription = contentDescription,
        onSuccess = { result ->
            scope.launch {
                if (result.result.dataSource == DataSource.MEMORY_CACHE) {
                    fadedAlpha.snapTo(1f)
                    settleScale.snapTo(1f)
                } else {
                    fadedAlpha.animateTo(1f, tween(250))
                    settleScale.animateTo(1f, tween(500))
                }
            }
        },
        onError = {
            scope.launch {
                fadedAlpha.snapTo(1f)
            }
        },
        modifier = modifier.then(Modifier.graphicsLayer {
            alpha = fadedAlpha.value
            scaleX = settleScale.value
            scaleY = settleScale.value
        }),
        placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
        error = painterResource(R.drawable.no_poster),
        fallback = painterResource(R.drawable.no_poster),
        contentScale = ContentScale.Crop
    )
}