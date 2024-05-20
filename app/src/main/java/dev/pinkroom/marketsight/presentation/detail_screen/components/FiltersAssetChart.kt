package dev.pinkroom.marketsight.presentation.detail_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.pinkroom.marketsight.domain.model.bars_asset.FilterHistoricalBar
import dev.pinkroom.marketsight.presentation.core.components.ButtonFilter
import dev.pinkroom.marketsight.presentation.core.theme.dimens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersAssetChart(
    modifier: Modifier = Modifier,
    selectedFilter: FilterHistoricalBar,
    filters: List<FilterHistoricalBar>,
    onChangeFilterAssetChart: (FilterHistoricalBar) -> Unit,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        filters.forEach { filter ->
            ButtonFilter(
                modifier = Modifier
                    .padding(horizontal = dimens.xSmallPadding),
                isSelected = filter == selectedFilter,
                text = "${filter.getValueTimeFrame()}${stringResource(id = filter.timeFrameString)}",
                borderWidth = dimens.smallWidth,
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = dimens.xSmallPadding),
                textStyle = MaterialTheme.typography.labelSmall,
                showLeadingIcon = false,
                onClick = { onChangeFilterAssetChart(filter) },
            )
        }
    }
}