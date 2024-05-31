package dev.pinkroom.marketsight.domain.model.common

import androidx.annotation.StringRes

data class SubInfoSymbols(
    @StringRes val stringResource: Int? = null,
    val name: String,
    val symbol: String,
    val isSubscribed: Boolean = false,
)
