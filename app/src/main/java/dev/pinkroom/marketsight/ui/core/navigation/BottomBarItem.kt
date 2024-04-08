package dev.pinkroom.marketsight.ui.core.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.pinkroom.marketsight.R

sealed class BottomBarItem(
    @StringRes val title: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    val route: String,
){
    data object Home: BottomBarItem(
        title = R.string.home,
        selectedIcon = R.drawable.icon_filled_home,
        unselectedIcon = R.drawable.icon_outline_home,
        route = Route.HomeScreen.route,
    )
    data object News: BottomBarItem(
        title = R.string.news,
        selectedIcon = R.drawable.icon_filled_article,
        unselectedIcon = R.drawable.icon_outline_article,
        route = Route.NewsScreen.route,
    )
}
