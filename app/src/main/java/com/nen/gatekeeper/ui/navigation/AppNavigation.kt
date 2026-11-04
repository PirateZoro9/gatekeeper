package com.nen.gatekeeper.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nen.gatekeeper.Detail
import com.nen.gatekeeper.Home
import com.nen.gatekeeper.Onboard
import com.nen.gatekeeper.Player
import com.nen.gatekeeper.data.local.AppPreferences
import com.nen.gatekeeper.ui.detail.DetailScreen
import com.nen.gatekeeper.ui.detail.DetailViewModel
import com.nen.gatekeeper.ui.home.HomeScreen
import com.nen.gatekeeper.ui.home.HomeViewModel
import com.nen.gatekeeper.ui.onboarding.OnboardingScreen
import com.nen.gatekeeper.ui.player.PlayerScreen
import com.nen.gatekeeper.ui.player.PlayerViewModel

@Composable
fun AppNavigation(
    backStack: NavBackStack<NavKey> = rememberNavBackStack(Home),
    innerPadding: PaddingValues,
    appPreferences: AppPreferences,
    modifier: Modifier = Modifier
) {
    val fadeTransition =
        fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))

    NavDisplay(
        modifier = modifier,
        transitionSpec = { fadeTransition },
        popTransitionSpec = { fadeTransition },
        predictivePopTransitionSpec = { fadeTransition },
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Onboard> {
                OnboardingScreen(
                    contentPadding = innerPadding,
                    onFinish = {
                        appPreferences.setOnboardingCompleted(true)
                        backStack.clear()
                        backStack.add(Home)
                    }
                )
            }
            entry<Home> {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    contentPadding = innerPadding,
                    onCardClick = { cardUrl ->
                        if (backStack.lastOrNull() !is Detail) {
                            backStack.add(Detail(detailUrl = cardUrl))
                        }
                    }
                )
            }
            entry<Detail> { detailKey ->
                val viewModel: DetailViewModel = hiltViewModel(
                    creationCallback = { factor: DetailViewModel.Factory ->
                        factor.create(detailKey.detailUrl)
                    }
                )
                DetailScreen(
                    viewModel = viewModel,
                    contentPadding = innerPadding,
                    onBackClick = {
                        backStack.removeLastOrNull()
                    },
                    onPlayClick = { media ->
                        if (backStack.lastOrNull() !is Player) {
                            backStack.add(Player(media))
                        }
                    }
                )
            }
            entry<Player> { playerKey ->
                val viewModel: PlayerViewModel = hiltViewModel(
                    creationCallback = { factory: PlayerViewModel.Factory ->
                        factory.create(playerKey.streamingData)
                    }
                )
                PlayerScreen(
                    viewModel = viewModel,
                    onBackClick = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}