package dev.pinkroom.marketsight.presentation.home_screen

import dev.pinkroom.marketsight.domain.model.assets.AssetFilter

sealed class HomeEvent {
    data class NewSearchInput(val value: String): HomeEvent()
    data class ChangeAssetFilter(val assetSelected: AssetFilter): HomeEvent()
}