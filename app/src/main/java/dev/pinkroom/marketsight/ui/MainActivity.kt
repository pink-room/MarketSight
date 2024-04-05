package dev.pinkroom.marketsight.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import dev.pinkroom.marketsight.ui.core.navigation.NavigationAppHost
import dev.pinkroom.marketsight.ui.core.navigation.Route
import dev.pinkroom.marketsight.ui.core.theme.MarketSightTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setOnExitAnimationListener{ screen ->
            val zoomY = ObjectAnimator.ofFloat(
                screen.iconView,
                View.TRANSLATION_Y,
                0f,
                -screen.view.height.toFloat()
            )
            zoomY.interpolator = LinearInterpolator()
            zoomY.duration = 200L
            zoomY.doOnEnd { screen.remove() }

            zoomY.start()
        }

        super.onCreate(savedInstanceState)

        setContent {
            //val navController = rememberNavController()

            MarketSightTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    /*Scaffold(
                        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
                        //bottomBar =
                    ) { padding ->
                        NavigationAppHost(
                            modifier = Modifier.padding(padding),
                            navController = navController,
                            startDestination = Route.NewsScreen
                        )
                    }*/
                    Text(text = "Hello", modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}