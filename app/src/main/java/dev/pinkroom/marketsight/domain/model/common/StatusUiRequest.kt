package dev.pinkroom.marketsight.domain.model.common

import androidx.annotation.StringRes

data class StatusUiRequest(
    val isLoading: Boolean = true,
    @StringRes val errorMessage: Int? = null,
)
