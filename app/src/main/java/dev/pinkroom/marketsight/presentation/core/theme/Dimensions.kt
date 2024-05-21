package dev.pinkroom.marketsight.presentation.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.pinkroom.marketsight.domain.model.news.ImageSize

class Dimensions(
    // Text
    val displayLarge: TextUnit = 57.sp,
    val displayMedium: TextUnit = 45.sp,
    val displaySmall: TextUnit = 36.sp,
    val headlineLarge: TextUnit = 32.sp,
    val headlineMedium: TextUnit = 28.sp,
    val headlineSmall: TextUnit = 24.sp,
    val titleLarge: TextUnit = 22.sp,
    val titleMedium: TextUnit = 19.sp,
    val titleSmall: TextUnit = 16.sp,
    val bodyLarge: TextUnit = 16.sp,
    val bodyMedium: TextUnit = 14.sp,
    val bodySmall: TextUnit = 12.sp,
    val labelLarge: TextUnit = 14.sp,
    val labelMedium: TextUnit = 12.sp,
    val labelSmall: TextUnit = 10.sp,

    // Padding
    val xSmallPadding: Dp = 4.dp,
    val smallPadding: Dp = 8.dp,
    val normalPadding: Dp = 16.dp,
    val largePadding: Dp = 20.dp,
    val xLargePadding: Dp = 32.dp,

    // Icon
    val smallIconSize: Dp = 16.dp,
    val normalIconSize: Dp = 24.dp,
    val largeIconSize: Dp = 38.dp,

    // Elevation
    val lowElevation: Dp = 2.dp,
    val normalElevation: Dp = 9.dp,
    val largeElevation: Dp = 12.dp,

    // Border Width
    val smallWidth: Dp = 1.5.dp,

    // Shape
    val smallShape: Dp = 10.dp,
    val normalShape: Dp = 15.dp,
    val largeShape: Dp = 22.dp,

    // Page Padding
    val horizontalPadding: Dp = 25.dp,
    val contentTopPadding: Dp = 10.dp,
    val contentBottomPadding: Dp = 40.dp,

    // Chart
    val spacingStartChartXAxis: Dp = 135.dp,
    val spacingTopChartYAxis: Dp = 100.dp,
    val widthBoxInfoChart: Dp = 300.dp,
    val heightBoxInfoChart: Dp = 80.dp,

    // Others
    val menuBottomPadding: Dp = 15.dp,
    val imageSizeMainNews: ImageSize = ImageSize.Small,
    val circlePageIndicatorSize: Dp = 9.dp,
    val spaceBetweenPageIndicator: Dp = 2.dp,
    val newsCard: Dp = 120.dp,
    val menuHeight: Dp = 100.dp,
    val liveNewsCardWidth: Dp = 270.dp,
    val liveNewsCardHeight: Dp = 100.dp,
    val searchInputHeight: Dp = 60.dp,
    val filterAssetCardWidth: Dp = 90.dp,
    val filterAssetCardHeight: Dp = 40.dp,
    val assetCardHeight: Dp = 90.dp,
    val emptyContentMaxHeight: Float = 0.95f,
    val bottomSheetHeight: Float = 0.45f,
    val heightChart: Dp = 380.dp,
    val heightBoxCurrentPrice: Dp = 50.dp,
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
private val smallDimensions = Dimensions(
    largeIconSize = 24.dp,
    normalIconSize = 15.dp,
    menuHeight = 70.dp,
    imageSizeMainNews = ImageSize.Small,
)
private val normalDimensions = Dimensions(
    imageSizeMainNews = ImageSize.Small,
)
private val largeDimensions = Dimensions(
    imageSizeMainNews = ImageSize.Large,
)