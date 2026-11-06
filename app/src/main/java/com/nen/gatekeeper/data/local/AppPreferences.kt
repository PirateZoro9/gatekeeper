package com.nen.gatekeeper.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("gatekeeper_prefs", Context.MODE_PRIVATE)
    private val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    private val KEY_SAVED_REPOS = "saved_repos"
    private val KEY_INSTALLED_PLUGINS = "installed_plugins"

    fun getSavedRepos(): Set<String> {
        return prefs.getStringSet(KEY_SAVED_REPOS, emptySet()) ?: emptySet()
    }

    fun setSavedRepos(repos: Set<String>) {
        prefs.edit().putStringSet(KEY_SAVED_REPOS, repos).apply()
    }

    fun getInstalledPluginsJson(): String? {
        return prefs.getString(KEY_INSTALLED_PLUGINS, null)
    }

    fun setInstalledPluginsJson(json: String) {
        prefs.edit().putString(KEY_INSTALLED_PLUGINS, json).apply()
    }

}