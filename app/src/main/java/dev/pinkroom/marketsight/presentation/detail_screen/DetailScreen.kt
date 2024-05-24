package dev.pinkroom.marketsight.presentation.detail_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.historicalBarFilters
import dev.pinkroom.marketsight.common.mockChartData
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.bars_asset.AssetChartInfo
import dev.pinkroom.marketsight.domain.model.bars_asset.CurrentPriceInfo
import dev.pinkroom.marketsight.domain.model.bars_asset.FilterHistoricalBar
import dev.pinkroom.marketsight.domain.model.bars_asset.TimeFrame
import dev.pinkroom.marketsight.domain.model.common.DateTimeUnit
import dev.pinkroom.marketsight.domain.model.common.StatusUiRequest
import dev.pinkroom.marketsight.domain.model.quotes_asset.QuoteAsset
import dev.pinkroom.marketsight.domain.model.trades_asset.TradeAsset
import dev.pinkroom.marketsight.presentation.core.components.PullToRefreshLazyColumn
import dev.pinkroom.marketsight.presentation.core.theme.Green
import dev.pinkroom.marketsight.presentation.core.theme.dimens
import dev.pinkroom.marketsight.presentation.detail_screen.components.AssetChart
import dev.pinkroom.marketsight.presentation.detail_screen.components.ErrorOnGetInfoRelatedToAsset
import dev.pinkroom.marketsight.presentation.detail_screen.components.FiltersAssetChart
import dev.pinkroom.marketsight.presentation.detail_screen.components.HeaderDetail
import dev.pinkroom.marketsight.presentation.detail_screen.components.QuoteInfo

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    statusMainInfo: StatusUiRequest,
    statusHistoricalBars: StatusUiRequest,
    asset: Asset,
    valueAsset: CurrentPriceInfo,
    assetChartInfo: AssetChartInfo,
    selectedFilterChart: FilterHistoricalBar,
    filtersAssetChart: List<FilterHistoricalBar>,
    statusQuote: StatusUiRequest,
    quotes: List<QuoteAsset>,
    statusTrade: StatusUiRequest,
    trades: List<TradeAsset>,
    onBack: () -> Unit,
    onEvent: (event: DetailEvent) -> Unit,
){
    PullToRefreshLazyColumn(
        modifier = modifier
            .fillMaxSize(),
        isRefreshing = false,
        onRefresh = {
            // TODO
        }
    ) {
        item {
            HeaderDetail(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.horizontalPadding),
                isLoading = statusMainInfo.isLoading,
                hasError = statusMainInfo.errorMessage != null,
                asset = asset,
                onBack = onBack,
                valueAsset = valueAsset,
            )
            ErrorOnGetInfoRelatedToAsset(
                modifier = Modifier
                    .fillParentMaxHeight(0.85f)
                    .fillParentMaxWidth(),
                statusUiRequest = statusMainInfo,
                onRetry = {
                    onEvent(DetailEvent.RetryToGetAssetInfo)
                }
            )
        }
        if (statusMainInfo.errorMessage == null){
            item {
                AssetChart(
                    modifier = Modifier
                        .padding(horizontal = dimens.horizontalPadding)
                        .padding(vertical = dimens.normalPadding)
                        .fillMaxWidth()
                        .height(dimens.heightChart),
                    chartInfo = assetChartInfo,
                    graphColor = Green,
                    colorText = MaterialTheme.colorScheme.onBackground,
                    isLoading = statusHistoricalBars.isLoading,
                    errorMessage = statusHistoricalBars.errorMessage,
                    infoToShow = { value, previousValue ->
                        onEvent(DetailEvent.ChangeActualPriceToShow(priceToShow = value, valueToCompare = previousValue))
                    },
                    onRetry = {
                        onEvent(DetailEvent.RetryToGetHistoricalBars)
                    },
                )
                FiltersAssetChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.horizontalPadding),
                    selectedFilter = selectedFilterChart,
                    filters = filtersAssetChart,
                    onChangeFilterAssetChart = {
                        onEvent(DetailEvent.ChangeFilterAssetChart(newFilter = it))
                    },
                )
            }
            item {
                QuoteInfo(
                    modifier = Modifier
                        .fillMaxWidth(),
                    statusQuote = statusQuote,
                    quotes = quotes,
                    onRetry = {
                        onEvent(DetailEvent.RetryToGetQuotesAsset)
                    }
                )
            }
            item {

            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun DetailScreenPreview() {
    DetailScreen(
        statusMainInfo = StatusUiRequest(isLoading = false, errorMessage = null),
        statusHistoricalBars = StatusUiRequest(isLoading = true),
        asset = Asset(
            id = "",
            symbol = "TSLA",
            name = "Tesla, Inc. Common Stock",
            isStock = true,
            exchange = "NSDQ"
        ),
        assetChartInfo = mockChartData(),
        onBack = {},
        onEvent = {},
        selectedFilterChart = FilterHistoricalBar(
            value = 20,
            timeFrameIntervalValues = TimeFrame.Month(value = 1),
            timeFrameString = R.string.year,
            dateTimeUnit = DateTimeUnit.Year,
        ),
        valueAsset = CurrentPriceInfo(),
        filtersAssetChart = historicalBarFilters,
        statusQuote = StatusUiRequest(
            isLoading = false, errorMessage = null,
        ),
        quotes = emptyList(),
        statusTrade = StatusUiRequest(
            isLoading = false, errorMessage = null,
        ),
        trades = emptyList(),
    )
}