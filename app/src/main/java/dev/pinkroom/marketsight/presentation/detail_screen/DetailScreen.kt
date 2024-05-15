package dev.pinkroom.marketsight.presentation.detail_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pinkroom.marketsight.common.mockChartData
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.common.StatusUiRequest
import dev.pinkroom.marketsight.presentation.core.components.PullToRefreshLazyColumn
import dev.pinkroom.marketsight.presentation.core.theme.dimens
import dev.pinkroom.marketsight.presentation.detail_screen.components.AssetChart
import dev.pinkroom.marketsight.presentation.detail_screen.components.HeaderDetail

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    statusMainInfo: StatusUiRequest,
    asset: Asset,
    onBack: () -> Unit,
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
                    .padding(horizontal = dimens.horizontalPadding, vertical = 10.dp)
                    .fillMaxWidth()
                    .height(380.dp),
                chartInfo = mockChartData(),
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun DetailScreenPreview(){
    DetailScreen(
        statusMainInfo = StatusUiRequest(isLoading = false),
        asset = Asset(
            id = "",
            symbol = "TSLA",
            name = "Tesla, Inc. Common Stock",
            isStock = true,
            exchange = "NSDQ"
        ),
        onBack = {}
    )
}