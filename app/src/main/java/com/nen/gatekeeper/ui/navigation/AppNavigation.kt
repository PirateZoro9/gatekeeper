package com.nen.gatekeeper.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
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
import com.nen.gatekeeper.ui.detail.DetailScreen
import com.nen.gatekeeper.ui.detail.DetailViewModel
import com.nen.gatekeeper.ui.home.HomeScreen
import com.nen.gatekeeper.ui.home.HomeViewModel

@Composable
fun AppNavigation(
    backStack: NavBackStack<NavKey> = rememberNavBackStack(Home),
    innerPadding: PaddingValues
) {
    val fadeTransition =
        fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))

    NavDisplay(
        transitionSpec = { fadeTransition },
        popTransitionSpec = { fadeTransition },
        predictivePopTransitionSpec = { fadeTransition },
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
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
            entry<Detail> { cardUrl ->
                val viewModel: DetailViewModel = hiltViewModel(
                    creationCallback = { factor: DetailViewModel.Factory ->
                        factor.create(cardUrl.detailUrl)
                    }
                )
                DetailScreen(
                    viewModel = viewModel,
                    contentPadding = innerPadding,
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}