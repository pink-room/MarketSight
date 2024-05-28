package dev.pinkroom.marketsight.presentation.detail_screen

import dev.pinkroom.marketsight.domain.model.assets.FilterAssetDetailInfo
import dev.pinkroom.marketsight.domain.model.bars_asset.FilterHistoricalBar

sealed class DetailEvent {
    data class ChangeFilterAssetChart(val newFilter: FilterHistoricalBar): DetailEvent()
    data class ChangeActualPriceToShow(val priceToShow: Double?, val valueToCompare: Double?): DetailEvent()
    data class ChangeFilterAssetDetailInfo(val newFilter: FilterAssetDetailInfo): DetailEvent()
    data object RetryToGetAssetInfo: DetailEvent()
    data object RetryToGetHistoricalBars: DetailEvent()
    data object RetryToSubscribeRealTimeAsset: DetailEvent()
    data object RetryToGetQuotesAsset: DetailEvent()
    data object RetryToGetTradesAsset: DetailEvent()
}