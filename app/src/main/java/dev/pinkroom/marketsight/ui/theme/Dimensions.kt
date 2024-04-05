package dev.pinkroom.marketsight.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Dimensions(
    // Text
    val displayLarge: TextUnit = 57.sp,
    val displayMedium: TextUnit = 45.sp,
    val displaySmall: TextUnit = 36.sp,
    val headlineLarge: TextUnit = 32.sp,
    val headlineMedium: TextUnit = 28.sp,
    val headlineSmall: TextUnit = 24.sp,
    val titleLarge: TextUnit = 22.sp,
    val titleMedium: TextUnit = 16.sp,
    val titleSmall: TextUnit = 14.sp,
    val bodyLarge: TextUnit = 16.sp,
    val bodyMedium: TextUnit = 14.sp,
    val bodySmall: TextUnit = 12.sp,
    val labelLarge: TextUnit = 14.sp,
    val labelMedium: TextUnit = 12.sp,
    val labelSmall: TextUnit = 11.sp,

    // Padding
    val xSmallPadding: Dp = 4.dp,
    val smallPadding: Dp = 8.dp,
    val normalPadding: Dp = 16.dp,
    val largePadding: Dp = 24.dp,
    val xLargePadding: Dp = 32.dp,
)

val dimens: Dimensions
    @Composable get() {
        val configuration = LocalConfiguration.current
        return when {
            configuration.screenHeightDp <= 700 -> smallDimensions
            configuration.screenHeightDp <= 960 -> normalDimensions
            else -> largeDimensions
        }
    }

// Here you will override the dimensions needed depending on the screen size
private val smallDimensions = Dimensions()
private val normalDimensions = Dimensions()
private val largeDimensions = Dimensions()