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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val backStack = rememberNavBackStack(Home)
            GateKeeperTheme {
                Scaffold { innerPadding ->
                    AppNavigation(backStack = backStack, innerPadding = innerPadding)
                }
            }
        }
    }
}
