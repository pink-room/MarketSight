package dev.pinkroom.marketsight.ui.theme

import androidx.compose.material3.Typography
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
val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = FontFamilyUbuntu),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = FontFamilyUbuntu),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = FontFamilyUbuntu),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = FontFamilyUbuntu),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = FontFamilyUbuntu),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = FontFamilyUbuntu),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = FontFamilyUbuntu),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = FontFamilyUbuntu),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = FontFamilyUbuntu),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = FontFamilyUbuntu),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = FontFamilyUbuntu),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = FontFamilyUbuntu),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = FontFamilyUbuntu),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = FontFamilyUbuntu),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = FontFamilyUbuntu)
)