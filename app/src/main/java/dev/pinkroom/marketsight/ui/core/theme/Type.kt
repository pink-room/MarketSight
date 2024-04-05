package dev.pinkroom.marketsight.ui.core.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import dev.pinkroom.marketsight.R


val FontFamilyUbuntu = FontFamily(
    Font(resId = R.font.ubuntu_regular, weight = FontWeight.Normal),
    Font(resId = R.font.ubuntu_bold, weight = FontWeight.Bold),
    Font(resId = R.font.ubuntu_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
    Font(resId = R.font.ubuntu_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(resId = R.font.ubuntu_light, weight = FontWeight.Light),
    Font(resId = R.font.ubuntu_light_italic, weight = FontWeight.Light, style = FontStyle.Italic),
    Font(resId = R.font.ubuntu_medium, weight = FontWeight.Medium),
    Font(resId = R.font.ubuntu_medium_italic, weight = FontWeight.Medium, style = FontStyle.Italic),
)

private val defaultTypography = Typography()
val Typography @Composable get() = Typography(
    // ----- START DISPLAY -----
    displayLarge = defaultTypography.displayLarge.copy(
        fontFamily = FontFamilyUbuntu,
        fontSize = dimens.displayLarge,
    ),
    displayMedium = defaultTypography.displayMedium.copy(
        fontFamily = FontFamilyUbuntu,
        fontSize = dimens.displayMedium,
    ),
    displaySmall = defaultTypography.displaySmall.copy(
        fontFamily = FontFamilyUbuntu,
        fontSize = dimens.displaySmall,
    ),
    // ----- END DISPLAY -----
    // ----- START HEADLINE -----
    headlineLarge = defaultTypography.headlineLarge.copy(
        fontFamily = FontFamilyUbuntu,
        fontSize = dimens.headlineLarge,
    ),
    headlineMedium = defaultTypography.headlineMedium.copy(
        fontFamily = FontFamilyUbuntu,
        fontSize = dimens.headlineMedium,
    ),
    headlineSmall = defaultTypography.headlineSmall.copy(
        fontFamily = FontFamilyUbuntu,
        fontSize = dimens.headlineSmall,
    ),
    // ----- END HEADLINE -----
    // ----- START TITLE -----
    titleLarge = defaultTypography.titleLarge.copy(
        fontFamily = FontFamilyUbuntu,
        fontSize = dimens.titleLarge,
    ),
    titleMedium = defaultTypography.titleMedium.copy(
        fontFamily = FontFamilyUbuntu,
        fontSize = dimens.titleMedium,
    ),
    titleSmall = defaultTypography.titleSmall.copy(
        fontFamily = FontFamilyUbuntu,
        fontSize = dimens.titleSmall,
    ),
    // ----- END TITLE -----
    // ----- START BODY -----
    bodyLarge = defaultTypography.bodyLarge.copy(
        fontFamily = FontFamilyUbuntu,
        fontSize = dimens.bodyLarge,
    ),
    bodyMedium = defaultTypography.bodyMedium.copy(
        fontFamily = FontFamilyUbuntu,
        fontSize = dimens.bodyMedium,
    ),
    bodySmall = defaultTypography.bodySmall.copy(
        fontFamily = FontFamilyUbuntu,
        fontSize = dimens.bodySmall,
    ),
    // ----- END BODY -----
    // ----- START LABEL -----
    labelLarge = defaultTypography.labelLarge.copy(
        fontFamily = FontFamilyUbuntu,
        fontSize = dimens.labelLarge,
    ),
    labelMedium = defaultTypography.labelMedium.copy(
        fontFamily = FontFamilyUbuntu,
        fontSize = dimens.labelMedium,
    ),
    labelSmall = defaultTypography.labelSmall.copy(
        fontFamily = FontFamilyUbuntu,
        fontSize = dimens.labelSmall,
    )
    // ----- END LABEL -----
)