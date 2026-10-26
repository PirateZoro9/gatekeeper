package com.nen.gatekeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Scaffold
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nen.gatekeeper.data.plugin.FourKHDHub
import com.nen.gatekeeper.data.repo.MediaRepository
import com.nen.gatekeeper.ui.detail.DetailScreen
import com.nen.gatekeeper.ui.detail.DetailViewModel
import com.nen.gatekeeper.ui.home.HomeBottomBar
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
            val backStack = rememberNavBackStack(Home)
            // Provide the ViewModel with the repository and active media source
            val viewModel: HomeViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        HomeViewModel(MediaRepository(FourKHDHub()))
                    }
                }
            )

            // Apply app theme and render the main home screen
            GateKeeperTheme {
                Scaffold(
                ) { innerPadding ->
                    NavDisplay(
                        transitionSpec = {
                            fadeIn(animationSpec = tween(500)) togetherWith fadeOut(
                                animationSpec = tween(
                                    500
                                )
                            )
                        },
                        popTransitionSpec = {
                            fadeIn(animationSpec = tween(500)) togetherWith fadeOut(
                                animationSpec = tween(
                                    500
                                )
                            )
                        },
                        predictivePopTransitionSpec = {
                            fadeIn(animationSpec = tween(500)) togetherWith fadeOut(
                                animationSpec = tween(
                                    500
                                )
                            )
                        },
                        backStack = backStack,
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        entryProvider = entryProvider {
                            entry<Home> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    contentPadding = innerPadding,
                                    onCardClick = { cardUrl ->
                                        if (backStack.lastOrNull() !is Detail) {
                                            backStack.add(Detail(cardUrl))
                                        }
                                    }
                                )
                            }
                            entry<Detail> { detailUrl ->
                                val viewModel: DetailViewModel = viewModel(
                                    factory = viewModelFactory {
                                        initializer {
                                            DetailViewModel(
                                                MediaRepository(FourKHDHub()),
                                                detailUrl.detailUrl
                                            )
                                        }
                                    }
                                )
                                DetailScreen(
                                    viewModel = viewModel,
                                    onBackClick = { backStack.removeLastOrNull() },
                                    contentPadding = innerPadding
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}