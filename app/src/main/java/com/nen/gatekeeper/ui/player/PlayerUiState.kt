package com.nen.gatekeeper.ui.player

import androidx.compose.runtime.Immutable

@Immutable
data class PlayerUiState(
    val title: String = "",
    val isPlaying: Boolean = true,
    val isBuffering: Boolean = true,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val areControlsVisible: Boolean = true,
    val errorMessage: String? = null,
    val isSeekSupported: Boolean = false
)