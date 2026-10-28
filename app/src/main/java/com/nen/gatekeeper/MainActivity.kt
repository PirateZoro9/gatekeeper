package com.nen.gatekeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.navigation3.runtime.rememberNavBackStack
import com.nen.gatekeeper.ui.navigation.AppNavigation
import com.nen.gatekeeper.ui.theme.GateKeeperTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity and entry point of the app.
 *
 * Sets up full-screen edge-to-edge layout, wires up the repository
 * to the ViewModel, and loads the home screen.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Extend app content behind status and navigation bars
        enableEdgeToEdge()

        setContent {
            val backStack = rememberNavBackStack(Home)
            // Provide the ViewModel with the repository and active media source
            // Apply app theme and render the main home screen
            GateKeeperTheme {
                Scaffold(
                ) { innerPadding ->
                    AppNavigation(backStack = backStack, innerPadding = innerPadding)
                }
            }
        }
    }
}