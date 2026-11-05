package com.nen.gatekeeper.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onFinish: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }

    BackHandler(enabled = currentStep > 1) {
        currentStep--
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(contentPadding)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STEP $currentStep OF 3",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            )
            TextButton(
                onClick = onFinish,
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.White.copy(alpha = 0.06f),
                    contentColor = Color(0xFF94A3B8)
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (currentStep) {
                1 -> WelcomeStep()
                2 -> HowItWorksStep()
                3 -> ReadyStep()
            }
        }

        Button(
            onClick = {
                if (currentStep < 3) {
                    currentStep++
                } else {
                    onFinish()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (currentStep == 3) Color(0xFF059669) else Color(0xFF2563EB)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text(
                text = if (currentStep == 3) "GET STARTED" else "CONTINUE",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}

@Composable
private fun WelcomeStep() {
    val infiniteTransition = rememberInfiniteTransition(label = "FloatTransition")
    val translateY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TranslateY"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer { translationY = translateY }
                .size(96.dp)
                .background(
                    color = Color(0xFF0D121C),
                    shape = RoundedCornerShape(26.dp)
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF3B82F6), Color(0xFF06B6D4), Color(0xFF6366F1))
                    ),
                    shape = RoundedCornerShape(26.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Movie,
                contentDescription = "GateKeeper Logo",
                tint = Color(0xFF60A5FA),
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            shape = RoundedCornerShape(50),
            color = Color(0xFF1E293B).copy(alpha = 0.7f),
            border = BorderStroke(1.dp, Color(0xFF334155).copy(alpha = 0.8f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Crafted with care by ",
                    color = Color(0xFF94A3B8),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "PirateZoro9 (Kabir)",
                    color = Color(0xFF22D3EE),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Welcome to GateKeeper",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Your personal stream engine & media player.",
            color = Color(0xFF94A3B8),
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(22.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A).copy(alpha = 0.7f),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "🛡️",
                    fontSize = 15.sp,
                    modifier = Modifier.padding(end = 10.dp)
                )
                Text(
                    text = "This app is completely empty. It doesn't host or provide any movies or series. You bring your own sources by adding extensions.",
                    color = Color(0xFF94A3B8),
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp)
                )
            }
        }
    }
}

@Composable
private fun HowItWorksStep() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF0F172A).copy(alpha = 0.75f),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "HOW GATEKEEPER WORKS",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF080B12).copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFF3B82F6).copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📦", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Community Repositories",
                            color = Color(0xFFF1F5F9),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Add any CS3 extension repo URL",
                            color = Color(0xFF94A3B8),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF080B12).copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚡", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Live Stream Verifier",
                            color = Color(0xFFF1F5F9),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Tests 206 seekability before play",
                            color = Color(0xFF94A3B8),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF080B12).copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFF06B6D4).copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎬", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Hardware Playback",
                            color = Color(0xFFF1F5F9),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Media3 ExoPlayer + Smart Caching",
                            color = Color(0xFF94A3B8),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "You're in Total Control",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "No tracking, no locked catalogs. GateKeeper adapts to whatever sources you install.",
            color = Color(0xFF94A3B8),
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun ReadyStep() {

}
