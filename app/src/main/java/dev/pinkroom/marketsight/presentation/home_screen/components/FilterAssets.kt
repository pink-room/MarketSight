package dev.pinkroom.marketsight.presentation.home_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import dev.pinkroom.marketsight.domain.model.assets.AssetFilter
import dev.pinkroom.marketsight.presentation.core.components.ButtonFilter
import dev.pinkroom.marketsight.presentation.core.theme.dimens
import dev.pinkroom.marketsight.presentation.core.theme.shimmerEffect

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterAssets(
    modifier: Modifier = Modifier,
    filters: List<AssetFilter>,
    isLoading: Boolean,
    onFilterClick: (AssetFilter) -> Unit
){
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.horizontalPadding, vertical = dimens.xSmallPadding),
        horizontalArrangement = Arrangement.Center,
    ) {
        if (!isLoading)
            filters.forEach { filter ->
                ButtonFilter(
                    modifier = Modifier
                        .padding(horizontal = dimens.smallPadding),
                    isSelected = filter.isSelected,
                    text = stringResource(id = filter.stringId),
                    onClick = {
                        onFilterClick(filter)
                    }
                )
            }
        else
            (0 until 2).forEach{ _ ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = dimens.smallPadding)
                        .width(dimens.filterAssetCardWidth)
                        .height(dimens.filterAssetCardHeight)
                        .clip(RoundedCornerShape(dimens.normalShape))
                        .shimmerEffect(),
                )
            }
    }
}