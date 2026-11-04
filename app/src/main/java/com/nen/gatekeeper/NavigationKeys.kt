package com.nen.gatekeeper

import androidx.navigation3.runtime.NavKey
import com.nen.gatekeeper.data.model.PlayerMedia
import kotlinx.serialization.Serializable

@Serializable
data object Home : NavKey

@Serializable
data class Detail(val detailUrl: String) : NavKey

@Serializable
data class Player(val streamingData: PlayerMedia) : NavKey

@Serializable
data object Onboard : NavKey