package dev.pinkroom.marketsight.presentation.detail_screen.components

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
import androidx.compose.ui.text.style.TextAlign
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.domain.model.common.StatusUiRequest
import dev.pinkroom.marketsight.presentation.core.theme.dimens

@Composable
fun ErrorOnGetAsset(
    modifier: Modifier = Modifier,
    statusUiRequest: StatusUiRequest,
    onRetry: () -> Unit,
) {
    statusUiRequest.errorMessage?.let {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = statusUiRequest.errorMessage),
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