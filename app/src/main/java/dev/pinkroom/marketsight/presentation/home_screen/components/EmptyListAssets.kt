package dev.pinkroom.marketsight.presentation.home_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.presentation.core.theme.dimens

@Composable
fun EmptyListAssets(
    modifier: Modifier = Modifier,
    isEmptyOnSearch: Boolean,
    hasError: Boolean,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isEmptyOnSearch) {
            Text(
                text = stringResource(id = R.string.empty_assets),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontStyle = FontStyle.Italic,
            )
        } else if (hasError) {
            Text(
                text = stringResource(id = R.string.error_on_getting_assets),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(dimens.smallPadding))
            Button(
                onClick = onRetry,
            ) {
                Text(
                    text = stringResource(id = R.string.retry)
                )
            }
        }
    }
}