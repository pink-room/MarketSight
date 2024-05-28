package dev.pinkroom.marketsight.presentation.detail_screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.assetDetailInfoFilters
import dev.pinkroom.marketsight.common.historicalBarFilters
import dev.pinkroom.marketsight.common.mockChartData
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.assets.FilterAssetDetailInfo
import dev.pinkroom.marketsight.domain.model.assets.TypeAssetDetailFilter
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
import dev.pinkroom.marketsight.presentation.detail_screen.components.FiltersInfoDetailAsset
import dev.pinkroom.marketsight.presentation.detail_screen.components.HeaderDetail
import dev.pinkroom.marketsight.presentation.detail_screen.components.QuoteInfo
import dev.pinkroom.marketsight.presentation.detail_screen.components.TradeInfo

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
    filtersAssetDetailInfo: List<FilterAssetDetailInfo>,
    selectedFilterDetailInfo: FilterAssetDetailInfo,
    onBack: () -> Unit,
    isRefreshing: Boolean,
    onEvent: (event: DetailEvent) -> Unit,
){
    PullToRefreshLazyColumn(
        modifier = modifier
            .fillMaxSize(),
        isRefreshing = isRefreshing,
        enabledPullToRefresh = {
            val mainInfoStatus = statusMainInfo.isLoading || statusMainInfo.errorMessage != null
            val barsInfo = statusHistoricalBars.isLoading
            val quoteInfo = statusQuote.isLoading
            val tradeInfo = statusTrade.isLoading
            !mainInfoStatus && !barsInfo && !quoteInfo && !tradeInfo && !isRefreshing
        },
        onRefresh = {
            onEvent(DetailEvent.Refresh)
        },
        contentPadding = PaddingValues(bottom = dimens.smallPadding)
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
                FiltersInfoDetailAsset(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.horizontalPadding),
                    filters = filtersAssetDetailInfo,
                    selectedFilter = selectedFilterDetailInfo,
                    onClick = {
                        onEvent(DetailEvent.ChangeFilterAssetDetailInfo(it))
                    }
                )
            }
            item {
                AnimatedContent(
                    targetState = selectedFilterDetailInfo.typeAssetDetailFilter,
                    transitionSpec = {
                        fadeIn(
                            animationSpec = tween(300, easing = EaseIn)
                        ).togetherWith(
                            fadeOut(
                                animationSpec = tween(300, easing = EaseOut)
                            )
                        ).using(
                            SizeTransform(
                                clip = false,
                                sizeAnimationSpec = { _, _ ->
                                    tween(300, easing = EaseInOut)
                                }
                            )
                        )
                    },
                    label = "State Detail Info Asset"
                ) { targetState ->
                    when (targetState) {
                        TypeAssetDetailFilter.Quotes -> QuoteInfo(
                            modifier = Modifier
                                .fillMaxWidth(),
                            statusQuote = statusQuote,
                            quotes = quotes,
                            onRetry = {
                                onEvent(DetailEvent.RetryToGetQuotesAsset)
                            }
                        )
                        TypeAssetDetailFilter.Trades -> TradeInfo(
                            statusTrade = statusTrade,
                            trades = trades,
                            onRetry = {
                                onEvent(DetailEvent.RetryToGetTradesAsset)
                            }
                        )
                    }
                }
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
        filtersAssetDetailInfo = assetDetailInfoFilters,
        selectedFilterDetailInfo = assetDetailInfoFilters.first(),
        isRefreshing = false,
    )
}