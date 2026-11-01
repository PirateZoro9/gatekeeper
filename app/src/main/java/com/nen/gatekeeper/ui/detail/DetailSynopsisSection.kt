package com.nen.gatekeeper.ui.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nen.gatekeeper.ui.theme.AppSpacing

// Expandable synopsis section
@Composable
fun DetailSynopsisSection(
    synopsis: String,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var showSeeMore by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.medium, vertical = AppSpacing.small)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 350,
                    easing = FastOutSlowInEasing
                )
            )
    ) {
        Text(
            text = synopsis,
            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { result ->
                if (!isExpanded && result.hasVisualOverflow) {
                    showSeeMore = true
                }
            },
            fontSize = 14.sp
        )
        if (showSeeMore) {
            TextButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier
                    .sizeIn(minHeight = 48.dp)
                    .semantics {
                        stateDescription = if (isExpanded) "Expanded" else "Collapsed"
                        onClick(
                            label = if (isExpanded) "Show less" else "Show more",
                            action = null
                        )
                    },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (isExpanded) "Show less" else "Show more",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
