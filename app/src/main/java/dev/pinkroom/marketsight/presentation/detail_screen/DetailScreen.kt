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
import dev.pinkroom.marketsight.domain.model.bars_asset.FilterHistoricalBar
import dev.pinkroom.marketsight.domain.model.bars_asset.TimeFrame
import dev.pinkroom.marketsight.domain.model.common.DateTimeUnit
import dev.pinkroom.marketsight.domain.model.common.StatusUiRequest
import dev.pinkroom.marketsight.presentation.core.components.PullToRefreshLazyColumn
import dev.pinkroom.marketsight.presentation.core.theme.Green
import dev.pinkroom.marketsight.presentation.core.theme.dimens
import dev.pinkroom.marketsight.presentation.detail_screen.components.AssetChart
import dev.pinkroom.marketsight.presentation.detail_screen.components.FiltersAssetChart
import dev.pinkroom.marketsight.presentation.detail_screen.components.HeaderDetail

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    statusMainInfo: StatusUiRequest,
    statusHistoricalBars: StatusUiRequest,
    asset: Asset,
    assetChartInfo: AssetChartInfo,
    selectedFilterChart: FilterHistoricalBar,
    filtersAssetChart: List<FilterHistoricalBar>,
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
                asset = asset,
                onBack = onBack,
            )
        }
        item {
            AssetChart(
                modifier = Modifier
                    .padding(horizontal = dimens.horizontalPadding)
                    .padding(bottom = dimens.largePadding)
                    .fillMaxWidth()
                    .height(dimens.heightChart),
                chartInfo = assetChartInfo,
                graphColor = Green,
                colorText = MaterialTheme.colorScheme.onBackground,
                isLoading = statusHistoricalBars.isLoading,
                infoToShow = {
                    onEvent(DetailEvent.ChangeActualPriceToShow(priceToShow = it))
                }
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
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun DetailScreenPreview() {
    DetailScreen(
        statusMainInfo = StatusUiRequest(isLoading = false),
        statusHistoricalBars = StatusUiRequest(isLoading = false),
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
        filtersAssetChart = historicalBarFilters,
    )
}