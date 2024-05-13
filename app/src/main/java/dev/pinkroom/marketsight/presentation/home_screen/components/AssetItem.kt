package dev.pinkroom.marketsight.presentation.home_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.presentation.core.theme.dimens

@Composable
fun AssetItem(
    modifier: Modifier = Modifier,
    asset: Asset,
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
        Column(
            modifier = modifierItem
                .fillMaxWidth()
                .padding(vertical = dimens.normalPadding, horizontal = dimens.normalPadding)
        ) {
            Text(
                text = asset.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
            )
            Text(
                text = asset.symbol,
                fontWeight = FontWeight.Medium,
            )
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
            name = "Tesla, Inc. Common Stock",
            isStock = true,
        ),
        onAssetClick = {}
    )
}