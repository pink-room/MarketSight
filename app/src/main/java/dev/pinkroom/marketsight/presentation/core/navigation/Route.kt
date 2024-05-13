package dev.pinkroom.marketsight.presentation.core.navigation

import dev.pinkroom.marketsight.presentation.core.navigation.Args.SYMBOL_ID

sealed class Route(val route: String){
    data object HomeScreen: Route("home-screen")
    data object NewsScreen: Route("news-screen")
    data object DetailScreen: Route("detail-screen/{$SYMBOL_ID}"){
        fun withId(id: String): String {
            return this.route.replace(oldValue = "{$SYMBOL_ID}", newValue = id)
        }
    }
}


object Args {
    const val SYMBOL_ID = "symbolId"
}