package dev.pinkroom.marketsight.domain.model.assets

import androidx.annotation.StringRes

data class AssetFilter(
    val typeAsset: TypeAsset,
    val isSelected: Boolean,
    @StringRes val stringId: Int,
    @StringRes val placeHolder: Int,
)
