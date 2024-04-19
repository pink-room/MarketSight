package dev.pinkroom.marketsight.ui.news_screen

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration

sealed class NewsAction{
    data class ShowSnackBar(
        @StringRes val message: Int,
        val duration: SnackbarDuration = SnackbarDuration.Short,
    ): NewsAction()
}
