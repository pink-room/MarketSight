package dev.pinkroom.marketsight.ui.news_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.ui.core.theme.dimens

@Composable
fun HeaderListNews(
    modifier: Modifier = Modifier,
    isFilterBtnEnabled: Boolean,
    onFilterClick: () -> Unit,
){
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.background,
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = dimens.horizontalPadding)
                .padding(bottom = dimens.contentTopPadding),
            text = stringResource(id = R.string.news),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Icon(
            modifier = Modifier
                .padding(horizontal = dimens.horizontalPadding)
                .size(dimens.normalIconSize)
                .clickable(
                    enabled = isFilterBtnEnabled,
                    onClick = onFilterClick,
                ),
            painter = painterResource(id = R.drawable.icon_filter_list),
            contentDescription = stringResource(id = R.string.filter_list_news_btn),
        )
    }
}