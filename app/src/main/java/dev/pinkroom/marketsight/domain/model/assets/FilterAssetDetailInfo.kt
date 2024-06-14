package dev.pinkroom.marketsight.domain.model.assets

import androidx.annotation.StringRes

data class FilterAssetDetailInfo(
    val typeAssetDetailFilter: TypeAssetDetailFilter,
    @StringRes val stringId: Int,
)
