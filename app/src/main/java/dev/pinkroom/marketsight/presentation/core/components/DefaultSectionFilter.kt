package dev.pinkroom.marketsight.presentation.core.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.pinkroom.marketsight.presentation.core.theme.dimens

@Composable
fun DefaultSectionFilter(
    modifier: Modifier = Modifier,
    title: String,
    showBottomDivider: Boolean = false,
    content: @Composable () -> Unit,
){
    HorizontalDivider(modifier = modifier.padding(horizontal = dimens.smallPadding))
    Text(
        modifier = Modifier
            .padding(horizontal = dimens.horizontalPadding)
            .padding(top = dimens.smallPadding),
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
    )
    content()
    if (showBottomDivider)
        HorizontalDivider(modifier = modifier
            .padding(horizontal = dimens.smallPadding, vertical = dimens.smallPadding)
        )
}