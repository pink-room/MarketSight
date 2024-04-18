package dev.pinkroom.marketsight.ui.news_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.ui.core.theme.dimens
import dev.pinkroom.marketsight.ui.news_screen.NewsEvent

@Composable
fun EmptyNewsList(
    modifier: Modifier = Modifier,
    errorMessage: Int,
    onEvent: (event: NewsEvent) -> Unit,
){
    Column(
        modifier = modifier
            .padding(horizontal = dimens.horizontalPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(dimens.largeIconSize),
            painter = painterResource(id = R.drawable.icon_error),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(dimens.smallPadding))
        Text(
            text = stringResource(id = errorMessage),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(dimens.normalPadding))
        Button(
            onClick = { onEvent(NewsEvent.RetryNews) }
        ) {
            Text(
                text = stringResource(id = R.string.retry)
            )
        }
    }
}