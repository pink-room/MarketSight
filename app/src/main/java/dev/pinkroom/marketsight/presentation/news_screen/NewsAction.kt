package dev.pinkroom.marketsight.presentation.news_screen

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration

sealed class NewsAction{
    data class ShowSnackBar(
        @StringRes val message: Int,
        val duration: SnackbarDuration = SnackbarDuration.Short,
        @StringRes val actionMessage: Int? = null,
        val retryEvent: NewsEvent? = null,
    ): NewsAction()

    data object CloseSnackBar: NewsAction()
}
