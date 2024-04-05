package dev.pinkroom.marketsight.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.pinkroom.marketsight.ui.theme.MarketSightTheme

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
            MarketSightTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    MarketSightTheme {
        Greeting("Android")
    }
}