package dev.pinkroom.marketsight.presentation.home_screen.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.presentation.core.theme.dimens
import dev.pinkroom.marketsight.presentation.core.theme.shimmerEffect

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.ListAssets(
    modifier: Modifier = Modifier,
    assets: List<Asset>,
    isEmptyOnSearch: Boolean,
    hasError: Boolean,
    isLoading: Boolean,
    onAssetClick: (Asset) -> Unit,
    onRetry: () -> Unit,
){
    if (isLoading)
        items(4) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(dimens.assetCardHeight)
                    .padding(horizontal = dimens.horizontalPadding, vertical = dimens.smallPadding)
                    .clip(RoundedCornerShape(dimens.normalShape))
                    .shimmerEffect(),
            )
        }
    else if (assets.isNotEmpty())
        items(
            items = assets,
            key = { it.symbol }
        ){
            AssetItem(
                modifier = modifier
                    .padding(horizontal = dimens.horizontalPadding, vertical = dimens.smallPadding)
                    .animateItemPlacement(),
                asset = it,
                onAssetClick = onAssetClick,
            )
        }
    else
        item {
            EmptyListAssets(
                modifier = modifier
                    .fillParentMaxWidth()
                    .fillParentMaxHeight(0.8f)
                    .padding(horizontal = dimens.horizontalPadding),
                isEmptyOnSearch = isEmptyOnSearch,
                hasError = hasError,
                onRetry = onRetry,
            )
        }
}