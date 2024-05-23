package dev.pinkroom.marketsight.presentation.detail_screen

import dev.pinkroom.marketsight.common.historicalBarFilters
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.bars_asset.AssetChartInfo
import dev.pinkroom.marketsight.domain.model.bars_asset.CurrentPriceInfo
import dev.pinkroom.marketsight.domain.model.bars_asset.FilterHistoricalBar
import dev.pinkroom.marketsight.domain.model.common.StatusUiRequest

data class DetailUiState(
    val statusMainInfo: StatusUiRequest = StatusUiRequest(),
    val statusQuotes: StatusUiRequest = StatusUiRequest(),
    val statusHistoricalBars: StatusUiRequest = StatusUiRequest(),
    val statusTrades: StatusUiRequest = StatusUiRequest(),
    val asset: Asset = Asset(),
    val currentPriceInfo: CurrentPriceInfo = CurrentPriceInfo(),
    val filtersHistoricalBar: List<FilterHistoricalBar> = historicalBarFilters,
    val selectedFilter: FilterHistoricalBar = filtersHistoricalBar.first(),
    val assetCharInfo: AssetChartInfo = AssetChartInfo(),
)
