package com.nen.gatekeeper

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Home : NavKey
@Serializable
data class Detail(val detailUrl: String) : NavKey