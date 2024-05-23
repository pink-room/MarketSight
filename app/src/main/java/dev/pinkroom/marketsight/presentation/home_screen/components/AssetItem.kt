package dev.pinkroom.marketsight.presentation.home_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dev.pinkroom.marketsight.common.formatToString
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.bars_asset.CurrentPriceInfo
import dev.pinkroom.marketsight.presentation.core.theme.Green
import dev.pinkroom.marketsight.presentation.core.theme.Red
import dev.pinkroom.marketsight.presentation.core.theme.dimens

@Composable
fun AssetItem(
    modifier: Modifier = Modifier,
    asset: Asset,
    value: CurrentPriceInfo? = null,
    onAssetClick: ((Asset) -> Unit)? = null,
){
    val modifierItem = if (onAssetClick != null) Modifier
        .clickable { onAssetClick(asset) }
    else Modifier
    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimens.lowElevation,
        ),
    ) {
        Row(
            modifier = modifierItem
                .padding(vertical = dimens.normalPadding, horizontal = dimens.normalPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
               modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    //maxLines = 2,
                )
                Text(
                    text = asset.symbol,
                    fontWeight = FontWeight.Medium,
                )
            }
            value?.let {
                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        modifier = Modifier,
                        textAlign = TextAlign.End,
                        text = "${value.price.formatToString(limitMaxDigit = false)}${value.fiatCurrency}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    value.percentage?.let {
                        value.priceDifference?.let {
                            val color = when {
                                value.priceDifference < 0.0 -> Red
                                value.priceDifference > 0.0 -> Green
                                else -> MaterialTheme.colorScheme.onPrimary
                            }
                            Text(
                                text = "${value.priceDifference.formatToString(limitMaxDigit = false)} (${value.percentage.formatToString(limitMaxDigit = false)}%)",
                                style = MaterialTheme.typography.labelMedium,
                                color = color
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AssetItemPreview(){
    AssetItem(
        asset = Asset(
            id = "asasas",
            symbol = "TSLA",
            exchange = "us_equetity",
            name = "Tesla, Inc. Common Stock Tesla, Inc. Common Stock",
            isStock = true,
        ),
        value = CurrentPriceInfo(price = 1500.0,percentage = 1.51, priceDifference = -2.0),
        onAssetClick = {}
    )
}