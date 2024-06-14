package dev.pinkroom.marketsight.presentation.home_screen

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration

sealed class HomeAction {
    data class ShowSnackBar(
        @StringRes val message: Int,
        val duration: SnackbarDuration = SnackbarDuration.Short,
        @StringRes val actionMessage: Int? = null,
    ): HomeAction()
}
