package dev.pinkroom.marketsight.presentation.home_screen

import androidx.annotation.StringRes
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.assetFilters
import dev.pinkroom.marketsight.domain.model.assets.AssetFilter

data class HomeUiState(
    val isLoading: Boolean = true,
    val searchInput: String? = null,
    @StringRes val placeHolder: Int = R.string.place_holder_stock,
    val filters: List<AssetFilter> = assetFilters,
)
