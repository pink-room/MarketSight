package dev.pinkroom.marketsight.presentation.detail_screen

import dev.pinkroom.marketsight.domain.model.bars_asset.FilterHistoricalBar

sealed class DetailEvent {
    data class ChangeFilterAssetChart(val newFilter: FilterHistoricalBar): DetailEvent()
    data class ChangeActualPriceToShow(val priceToShow: Double?): DetailEvent()
}