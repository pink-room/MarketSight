package dev.pinkroom.marketsight.ui.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.ui.core.theme.dimens

@Composable
fun NavigationBottomBar(
    navController: NavHostController,
    startDestination: Route.HomeScreen,
){
    val items = listOf(
        BottomBarItem.Home,
        BottomBarItem.News,
    )

    var bottomBarState by rememberSaveable {
        mutableStateOf(true)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    bottomBarState = when (currentRoute) {
        Route.HomeScreen.route, Route.NewsScreen.route -> true
        else -> false
    }

    AnimatedVisibility(
        visible = bottomBarState,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.horizontalPadding)
                    .padding(bottom = dimens.menuBottomPadding),
                shape = RoundedCornerShape(dimens.smallShape),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = dimens.lowElevation,
                ),
            ) {
                NavigationBar(
                    containerColor = Color.Transparent
                ) {
                    items.forEach { bottomBarItem ->
                        AddItem(
                            currentRoute = currentRoute,
                            item = bottomBarItem,
                            onClick = {
                                navController.navigate(bottomBarItem.route) {
                                    popUpTo(startDestination.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun RowScope.AddItem(
    currentRoute: String?,
    item: BottomBarItem,
    onClick: () -> Unit,
){
    val isThisItemTheCurrentRoute = currentRoute == item.route
    val icon = if (isThisItemTheCurrentRoute) item.selectedIcon
    else item.unselectedIcon
    val sizeIcon = animateDpAsState(
        targetValue = if (isThisItemTheCurrentRoute) dimens.largeIconSize else dimens.normalIconSize,
        animationSpec = tween(
            durationMillis = 200,
        ),
        label = "Animation Size Icon Bottom Item",
    )

    val contentDesc = "${stringResource(id = item.title)} ${stringResource(id = R.string.desc_icon_bottom_bar)}"

    NavigationBarItem(
        selected = isThisItemTheCurrentRoute,
        icon = {
            Icon(
                modifier = Modifier
                    .size(sizeIcon.value),
                painter = painterResource(id = icon),
                contentDescription = contentDesc,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent,
        ),
        onClick = onClick,
    )
}