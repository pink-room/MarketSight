package dev.pinkroom.marketsight.presentation.detail_screen.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.Constants.DEFAULT_LIMIT_TRADES_ASSET
import dev.pinkroom.marketsight.common.getTimeString
import dev.pinkroom.marketsight.domain.model.common.StatusUiRequest
import dev.pinkroom.marketsight.domain.model.trades_asset.TradeAsset
import dev.pinkroom.marketsight.presentation.core.theme.dimens
import dev.pinkroom.marketsight.presentation.core.theme.shimmerEffect

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TradeInfo(
    modifier: Modifier = Modifier,
    statusTrade: StatusUiRequest,
    trades: List<TradeAsset>,
    onRetry: () -> Unit,
) {
    if (statusTrade.isLoading)
        Box(
            modifier = Modifier
                .height(dimens.maxHeightQuoteInfo)
                .fillMaxWidth()
                .padding(horizontal = dimens.horizontalPadding)
                .shadow(
                    elevation = dimens.lowElevation,
                    shape = RoundedCornerShape(dimens.normalShape)
                )
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(dimens.normalShape)
                )
                .shimmerEffect()
        )
    else if (statusTrade.errorMessage != null)
        ErrorOnGetInfoRelatedToAsset(
            modifier = Modifier
                .height(dimens.maxHeightQuoteInfo)
                .fillMaxWidth()
                .padding(horizontal = dimens.horizontalPadding)
                .shadow(
                    elevation = dimens.lowElevation,
                    shape = RoundedCornerShape(dimens.normalShape)
                )
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(dimens.normalShape)
                ),
            statusUiRequest = statusTrade,
            onRetry = onRetry,
            colorBtn = MaterialTheme.colorScheme.background,
        )
    else
        LazyColumn(
            modifier = modifier
                .heightIn(max = dimens.sizeTradeItem * DEFAULT_LIMIT_TRADES_ASSET)
                .padding(horizontal = dimens.horizontalPadding)
                .shadow(
                    elevation = dimens.lowElevation,
                    shape = RoundedCornerShape(dimens.normalShape)
                )
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(dimens.normalShape)
                ),
            contentPadding = PaddingValues(start = dimens.smallPadding, end = dimens.smallPadding, bottom = dimens.smallPadding)
        ) {
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.primary)
                        .padding(top = dimens.smallPadding),
                ) {
                    TradeItem(
                        modifier = Modifier
                            .fillMaxWidth(),
                        time = stringResource(id = R.string.time).uppercase(),
                        tradeSize = stringResource(id = R.string.size).uppercase(),
                        tradePrice = stringResource(id = R.string.price).uppercase(),
                        header = true,
                    )
                }
            }
            items(
                items = trades,
                key = { it.id }
            ) { trade ->
                TradeItem(
                    modifier = Modifier
                        .padding(vertical = dimens.spaceBetweenItemsInfoAsset)
                        .fillMaxWidth(),
                    time = trade.timeStamp.getTimeString(),
                    tradeSize = trade.tradeSize.toString(),
                    tradePrice = trade.tradePrice.toString(),
                )
            }
            item {
                if(trades.isEmpty())
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            modifier = Modifier.weight(2f),
                            text = stringResource(id = R.string.not_available_data),
                        )
                    }
            }
        }
}

@Composable
fun TradeItem(
    modifier: Modifier = Modifier,
    time: String,
    tradePrice: String,
    tradeSize: String,
    header: Boolean = false,
    style: TextStyle? = null,
) {
    val fontWeight = if (header) FontWeight.Bold else FontWeight.Normal
    val textStyle = style ?: if (header) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelMedium

    Row(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier
                .weight(1f),
            text = time,
            fontWeight = fontWeight,
            style = textStyle,
        )
        Text(
            modifier = Modifier
                .weight(1f),
            text = tradePrice,
            fontWeight = fontWeight,
            style = textStyle,
        )
        Text(
            modifier = Modifier
                .weight(1f),
            text = tradeSize,
            fontWeight = fontWeight,
            style = textStyle,
            textAlign = TextAlign.End
        )
    }
}