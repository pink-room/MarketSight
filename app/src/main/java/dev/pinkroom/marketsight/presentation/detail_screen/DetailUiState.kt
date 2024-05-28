package dev.pinkroom.marketsight.presentation.detail_screen

import dev.pinkroom.marketsight.common.assetDetailInfoFilters
import dev.pinkroom.marketsight.common.historicalBarFilters
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.assets.FilterAssetDetailInfo
import dev.pinkroom.marketsight.domain.model.bars_asset.AssetChartInfo
import dev.pinkroom.marketsight.domain.model.bars_asset.CurrentPriceInfo
import dev.pinkroom.marketsight.domain.model.bars_asset.FilterHistoricalBar
import dev.pinkroom.marketsight.domain.model.common.StatusUiRequest
import dev.pinkroom.marketsight.domain.model.quotes_asset.QuoteAsset
import dev.pinkroom.marketsight.domain.model.trades_asset.TradeAsset

data class DetailUiState(
    val statusMainInfo: StatusUiRequest = StatusUiRequest(),
    val statusQuotes: StatusUiRequest = StatusUiRequest(),
    val statusHistoricalBars: StatusUiRequest = StatusUiRequest(),
    val statusTrades: StatusUiRequest = StatusUiRequest(),
    val asset: Asset = Asset(),
    val currentPriceInfo: CurrentPriceInfo = CurrentPriceInfo(),
    val filtersHistoricalBar: List<FilterHistoricalBar> = historicalBarFilters,
    val selectedFilterHistorical: FilterHistoricalBar = filtersHistoricalBar.first(),
    val assetCharInfo: AssetChartInfo = AssetChartInfo(),
    val latestQuotes: List<QuoteAsset> = emptyList(),
    val latestTrades: List<TradeAsset> = emptyList(),
    val filtersAssetDetailInfo: List<FilterAssetDetailInfo> = assetDetailInfoFilters,
    val selectedFilterDetailInfo: FilterAssetDetailInfo = filtersAssetDetailInfo.first(),
)
