package dev.pinkroom.marketsight.ui.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.pinkroom.marketsight.ui.detail_screen.DetailScreen
import dev.pinkroom.marketsight.ui.home_screen.HomeScreen
import dev.pinkroom.marketsight.ui.news_screen.NewsScreen

@Composable
fun NavigationAppHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Route,
){
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        composable(Route.HomeScreen.route) {
            HomeScreen()
        }
        composable(Route.NewsScreen.route) {
            NewsScreen()
        }
        composable(Route.DetailScreen.route) {
            DetailScreen()
        }
    }
}