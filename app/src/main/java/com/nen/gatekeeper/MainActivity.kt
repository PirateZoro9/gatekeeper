package com.nen.gatekeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.navigation3.runtime.rememberNavBackStack
import com.nen.gatekeeper.data.local.AppPreferences
import com.nen.gatekeeper.ui.navigation.AppNavigation
import com.nen.gatekeeper.ui.theme.GateKeeperTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var appPreferences: AppPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val startDestination = if (appPreferences.isOnboardingCompleted()) Home else Onboard
            val backStack = rememberNavBackStack(startDestination)
            GateKeeperTheme {
                Scaffold { innerPadding ->
                    AppNavigation(
                        backStack = backStack,
                        innerPadding = innerPadding,
                        appPreferences = appPreferences
                    )
                }
            }
        }
    }
}
