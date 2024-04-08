package dev.pinkroom.marketsight.ui.core.navigation

import dev.pinkroom.marketsight.ui.core.navigation.Args.SYMBOL_ID

sealed class Route(val route: String){
    data object HomeScreen: Route("home-screen")
    data object NewsScreen: Route("news-screen")
    data object DetailScreen: Route("detail-screen/{$SYMBOL_ID}"){
        fun withSymbol(symbolId: String): String {
            return this.route.replace(oldValue = "{$SYMBOL_ID}", newValue = symbolId)
        }
    }
}


object Args {
    const val SYMBOL_ID = "symbolId"
}