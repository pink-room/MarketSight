package dev.pinkroom.marketsight.presentation.detail_screen

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration

sealed class DetailAction {
    data object NavigateToHomeEmptyId: DetailAction()
    data class ShowSnackBar(
        @StringRes val message: Int,
        val duration: SnackbarDuration = SnackbarDuration.Short,
        @StringRes val actionMessage: Int? = null,
    ): DetailAction()
}
