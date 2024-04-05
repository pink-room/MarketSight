package dev.pinkroom.marketsight.ui.core.navigation

sealed class Route(val route: String){
    data object HomeScreen: Route("home-screen")
    data object NewsScreen: Route("news-screen")
    data object DetailScreen: Route("detail-screen")
}
