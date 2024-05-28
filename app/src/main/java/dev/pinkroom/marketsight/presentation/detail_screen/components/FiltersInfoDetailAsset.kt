package dev.pinkroom.marketsight.presentation.detail_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.pinkroom.marketsight.common.assetDetailInfoFilters
import dev.pinkroom.marketsight.domain.model.assets.FilterAssetDetailInfo
import dev.pinkroom.marketsight.presentation.core.theme.dimens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersInfoDetailAsset(
    modifier: Modifier = Modifier,
    filters: List<FilterAssetDetailInfo>,
    selectedFilter: FilterAssetDetailInfo,
    onClick: (FilterAssetDetailInfo) -> Unit,
) {
    FlowRow(
        modifier = modifier,
    ) {
        filters.forEach { filter ->
            FilterInfoDetailItem(
                filter = filter,
                isSelected = selectedFilter == filter,
                onCLick = onClick,
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun PreviewFiltersInfoDetailAsset() {
    FiltersInfoDetailAsset(
        filters = assetDetailInfoFilters,
        selectedFilter = assetDetailInfoFilters.first(),
        onClick = {

        }
    )
}

@Composable
fun FilterInfoDetailItem(
    modifier: Modifier = Modifier,
    filter: FilterAssetDetailInfo,
    isSelected: Boolean,
    onCLick: (FilterAssetDetailInfo) -> Unit,
) {
    val colorBackground = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val shape = RoundedCornerShape(dimens.smallShape)
    Box(
        modifier = modifier
            .background(color = colorBackground, shape = shape)
            .clip(shape)
            .clickable(
                onClick = { onCLick(filter) }
            )
            .padding(dimens.smallPadding),
    ) {
        Text(
            text = stringResource(id = filter.stringId)
        )
    }
}