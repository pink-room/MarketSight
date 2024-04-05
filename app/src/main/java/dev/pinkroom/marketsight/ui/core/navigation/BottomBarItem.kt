package dev.pinkroom.marketsight.ui.core.navigation

import androidx.annotation.DrawableRes
import dev.pinkroom.marketsight.R

sealed class BottomBarItem(
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    val route: String,
){
    data object Home: BottomBarItem(
        selectedIcon = R.drawable.icon_filled_home,
        unselectedIcon = R.drawable.icon_outline_home,
        route = Route.HomeScreen.route,
    )
    data object News: BottomBarItem(
        selectedIcon = R.drawable.icon_filled_article,
        unselectedIcon = R.drawable.icon_outline_article,
        route = Route.NewsScreen.route,
    )
}
