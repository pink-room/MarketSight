package dev.pinkroom.marketsight.ui.core.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.pinkroom.marketsight.ui.core.navigation.Args.SYMBOL_ID
import dev.pinkroom.marketsight.ui.core.util.ObserveAsEvents
import dev.pinkroom.marketsight.ui.detail_screen.DetailScreen
import dev.pinkroom.marketsight.ui.home_screen.HomeScreen
import dev.pinkroom.marketsight.ui.home_screen.HomeViewModel
import dev.pinkroom.marketsight.ui.news_screen.NewsAction
import dev.pinkroom.marketsight.ui.news_screen.NewsScreen
import dev.pinkroom.marketsight.ui.news_screen.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationAppHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Route,
    onShowSnackBar: (message: String, duration: SnackbarDuration) -> Unit,
){
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        composable(
            route = Route.HomeScreen.route,
        ) {
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeScreen()
        }
        composable(
            route = Route.NewsScreen.route,
        ) {
            val viewModel = hiltViewModel<NewsViewModel>()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

            ObserveAsEvents(viewModel.action){ action ->
                when(action){
                    is NewsAction.ShowSnackBar -> {
                        onShowSnackBar(context.getString(action.message), action.duration)
                    }
                }
            }

            NewsScreen(
                news = uiState.news,
                mainNews = uiState.mainNews,
                realTimeNews = uiState.realTimeNews,
                symbols = uiState.symbols,
                sortBy = uiState.sortBy,
                sortItems = uiState.sort,
                endDate = uiState.endDateSort,
                startDate = uiState.startDateSort,
                startSelectableDates = uiState.startSelectableDates,
                endSelectableDates = uiState.endSelectableDates,
                isLoading = uiState.isLoading,
                isLoadingMoreNews = uiState.isLoadingMoreItems,
                isRefreshing = uiState.isRefreshing,
                isToShowFilters = uiState.isToShowFilters,
                errorMessage = uiState.errorMessage,
                onEvent = viewModel::onEvent
            )
        }
        composable(
            route = Route.DetailScreen.route,
            arguments = listOf(
                navArgument(name = SYMBOL_ID){
                    type = NavType.StringType
                },
            )
        ) {
            DetailScreen()
        }
    }
}