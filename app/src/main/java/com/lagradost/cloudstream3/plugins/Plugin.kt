package com.lagradost.cloudstream3.plugins

import com.lagradost.cloudstream3.MainAPI

// Base class inherited by plugin entry points in compiled .cs3 dex files
open class Plugin {
    val registeredApis = mutableListOf<MainAPI>()

    // Called when the plugin is loaded into memory
    open fun load(context: Any? = null) {}

    // Scrapers call this to register themselves with GateKeeper
    fun registerMainAPI(element: MainAPI) {
        registeredApis.add(element)
    }
}