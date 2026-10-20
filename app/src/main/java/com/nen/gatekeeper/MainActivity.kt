package com.nen.gatekeeper

 import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nen.gatekeeper.data.plugin.Hdhub4u
import com.nen.gatekeeper.data.repo.MediaRepository
import com.nen.gatekeeper.ui.home.BottomBar
import com.nen.gatekeeper.ui.home.HomeScreen
import com.nen.gatekeeper.ui.home.HomeViewModel
import com.nen.gatekeeper.ui.theme.GateKeeperTheme

/**
 * Main activity and entry point of the app.
 *
 * Sets up full-screen edge-to-edge layout, wires up the repository
 * to the ViewModel, and loads the home screen.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Extend app content behind status and navigation bars
        enableEdgeToEdge()

        setContent {
            // Provide the ViewModel with the repository and active media source
            val viewModel: HomeViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        HomeViewModel(MediaRepository(Hdhub4u()))
                    }
                }
            )

            // Apply app theme and render the main home screen
            GateKeeperTheme {
                Scaffold(
                    bottomBar = { BottomBar() }
                ) { innerPadding ->
                    HomeScreen(
                        viewModel = viewModel,
                        contentPadding = innerPadding
                    )
                }
            }
        }
    }
}