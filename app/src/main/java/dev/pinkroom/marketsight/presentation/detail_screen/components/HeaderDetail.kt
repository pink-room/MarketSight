package dev.pinkroom.marketsight.presentation.detail_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.bars_asset.CurrentPriceInfo
import dev.pinkroom.marketsight.presentation.core.theme.dimens
import dev.pinkroom.marketsight.presentation.core.theme.shimmerEffect
import dev.pinkroom.marketsight.presentation.home_screen.components.AssetItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderDetail(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    asset: Asset,
    valueAsset: CurrentPriceInfo,
    hasError: Boolean,
    onBack: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
      modifier = modifier,
    ) {
        CenterAlignedTopAppBar(
            modifier = Modifier
                .fillMaxWidth(),
            navigationIcon = {
                Icon(
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onBack,
                        ),
                    painter = painterResource(id = R.drawable.icon_back), contentDescription = null,
                )
            },
            title = {
                Text(
                    text = stringResource(id = R.string.overview),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
            }
        )
        if (isLoading)
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(dimens.assetCardHeight)
                    .clip(RoundedCornerShape(dimens.normalShape))
                    .shimmerEffect(),
            )
        else if (!hasError)
            AssetItem(
                asset = asset,
                value = valueAsset,
            )
    }
}