package dev.pinkroom.marketsight.presentation.core.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.pinkroom.marketsight.presentation.core.navigation.Args.SYMBOL_ID
import dev.pinkroom.marketsight.presentation.core.util.ObserveAsEvents
import dev.pinkroom.marketsight.presentation.detail_screen.DetailAction
import dev.pinkroom.marketsight.presentation.detail_screen.DetailEvent
import dev.pinkroom.marketsight.presentation.detail_screen.DetailScreen
import dev.pinkroom.marketsight.presentation.detail_screen.DetailViewModel
import dev.pinkroom.marketsight.presentation.home_screen.HomeAction
import dev.pinkroom.marketsight.presentation.home_screen.HomeScreen
import dev.pinkroom.marketsight.presentation.home_screen.HomeViewModel
import dev.pinkroom.marketsight.presentation.news_screen.NewsAction
import dev.pinkroom.marketsight.presentation.news_screen.NewsScreen
import dev.pinkroom.marketsight.presentation.news_screen.NewsViewModel
import kotlinx.coroutines.launch

@Composable
fun NavigationAppHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Route,
    onShowSnackBar: suspend (message: String, duration: SnackbarDuration, action: String?) -> Boolean,
    closeSnackBar: () -> Unit,
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        composable(
            route = Route.HomeScreen.route,
        ) {
            val viewModel = hiltViewModel<HomeViewModel>()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

            ObserveAsEvents(viewModel.action){ action ->
                when(action){
                    is HomeAction.ShowSnackBar -> scope.launch {
                        onShowSnackBar(
                            context.getString(action.message),
                            action.duration,
                            action.actionMessage?.let { msg -> context.getString(msg) },
                        )
                    }
                }
            }

            HomeScreen(
                isLoading = uiState.isLoading,
                placeHolder = uiState.placeHolder,
                searchInput = uiState.searchInput,
                filters = uiState.filters,
                assets = uiState.assets,
                isEmptyOnSearch = uiState.isEmptyOnSearch,
                isRefreshing = uiState.isRefreshing,
                hasError = uiState.hasError,
                onEvent = viewModel::onEvent,
                navigateToAssetDetailScreen = {
                    navController.navigate(Route.DetailScreen.withId(it.id))
                }
            )
        }
        composable(
            route = Route.NewsScreen.route,
        ) {
            val viewModel = hiltViewModel<NewsViewModel>()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

            ObserveAsEvents(viewModel.action){ action ->
                when(action){
                    is NewsAction.ShowSnackBar -> {
                        scope.launch {
                            val result = onShowSnackBar(
                                context.getString(action.message),
                                action.duration,
                                action.actionMessage?.let { msg -> context.getString(msg) },
                            )
                            if (result) {
                                action.retryEvent?.let { viewModel.onEvent(event = it) }
                            }
                        }
                    }
                    NewsAction.CloseSnackBar -> closeSnackBar()
                }
            }

            NewsScreen(
                news = uiState.news,
                mainNews = uiState.mainNews,
                realTimeNews = uiState.realTimeNews,
                filters = uiState.filters,
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
            val viewModel = hiltViewModel<DetailViewModel>()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

            ObserveAsEvents(viewModel.action){ action ->
                when(action){
                    DetailAction.NavigateToHomeEmptyId -> navController.popBackStack()
                    is DetailAction.ShowSnackBar -> {
                        scope.launch {
                            val result = onShowSnackBar(
                                context.getString(action.message),
                                action.duration,
                                action.actionMessage?.let { msg -> context.getString(msg) },
                            )
                            if (result && action.actionMessage != null){
                                viewModel.onEvent(DetailEvent.RetryToSubscribeRealTimeAsset)
                            }
                        }
                    }
                }
            }

            DetailScreen(
                asset = uiState.asset,
                valueAsset = uiState.currentPriceInfo,
                statusMainInfo = uiState.statusMainInfo,
                statusHistoricalBars = uiState.statusHistoricalBars,
                assetChartInfo = uiState.assetCharInfo,
                selectedFilterChart = uiState.selectedFilterHistorical,
                filtersAssetChart = uiState.filtersHistoricalBar,
                trades = uiState.latestTrades,
                statusTrade = uiState.statusTrades,
                quotes = uiState.latestQuotes,
                statusQuote = uiState.statusQuotes,
                filtersAssetDetailInfo = uiState.filtersAssetDetailInfo,
                selectedFilterDetailInfo = uiState.selectedFilterDetailInfo,
                isRefreshing = uiState.isRefreshing,
                onBack = { navController.navigateUp() },
                onEvent = viewModel::onEvent,
            )
        }
    }
}