package dev.pinkroom.marketsight.ui.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.pinkroom.marketsight.ui.core.navigation.Args.SYMBOL_ID
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
        composable(
            route = Route.HomeScreen.route,
        ) {
            HomeScreen()
        }
        composable(
            route = Route.NewsScreen.route,
        ) {
            NewsScreen()
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