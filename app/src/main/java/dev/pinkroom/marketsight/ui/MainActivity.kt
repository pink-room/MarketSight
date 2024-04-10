package dev.pinkroom.marketsight.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.pinkroom.marketsight.ui.core.navigation.NavigationAppHost
import dev.pinkroom.marketsight.ui.core.navigation.NavigationBottomBar
import dev.pinkroom.marketsight.ui.core.navigation.Route
import dev.pinkroom.marketsight.ui.core.theme.MarketSightTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val startDestination = Route.HomeScreen

            MarketSightTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NavigationBottomBar(
                                navController = navController,
                                startDestination = startDestination,
                            )
                        }
                    ) {  padding ->
                        NavigationAppHost(
                            modifier = Modifier.padding(padding),
                            navController = navController,
                            startDestination = startDestination,
                        )
                    }
                }
            }
        }
    }
}