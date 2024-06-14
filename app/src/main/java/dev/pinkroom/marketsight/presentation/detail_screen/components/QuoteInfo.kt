package dev.pinkroom.marketsight.presentation.detail_screen.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.Constants.DEFAULT_LIMIT_QUOTES_ASSET
import dev.pinkroom.marketsight.domain.model.common.StatusUiRequest
import dev.pinkroom.marketsight.domain.model.quotes_asset.QuoteAsset
import dev.pinkroom.marketsight.presentation.core.theme.Green
import dev.pinkroom.marketsight.presentation.core.theme.Red
import dev.pinkroom.marketsight.presentation.core.theme.dimens
import dev.pinkroom.marketsight.presentation.core.theme.shimmerEffect

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuoteInfo(
    modifier: Modifier = Modifier,
    statusQuote: StatusUiRequest,
    quotes: List<QuoteAsset>,
    onRetry: () -> Unit,
) {
    if (statusQuote.isLoading)
        Box(
            modifier = Modifier
                .height(dimens.maxHeightQuoteInfo)
                .fillMaxWidth()
                .padding(horizontal = dimens.horizontalPadding)
                .shadow(
                    elevation = dimens.lowElevation,
                    shape = RoundedCornerShape(dimens.normalShape)
                )
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(dimens.normalShape)
                )
                .shimmerEffect()
        )
    else if (statusQuote.errorMessage != null)
        ErrorOnGetInfoRelatedToAsset(
            modifier = Modifier
                .height(dimens.maxHeightQuoteInfo)
                .fillMaxWidth()
                .padding(horizontal = dimens.horizontalPadding)
                .shadow(
                    elevation = dimens.lowElevation,
                    shape = RoundedCornerShape(dimens.normalShape)
                )
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(dimens.normalShape)
                ),
            statusUiRequest = statusQuote,
            onRetry = onRetry,
            colorBtn = MaterialTheme.colorScheme.background,
        )
    else
        LazyColumn(
            modifier = modifier
                .heightIn(max = dimens.sizeQuoteItem*DEFAULT_LIMIT_QUOTES_ASSET)
                .padding(horizontal = dimens.horizontalPadding)
                .shadow(
                    elevation = dimens.lowElevation,
                    shape = RoundedCornerShape(dimens.normalShape)
                )
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(dimens.normalShape)
                ),
            contentPadding = PaddingValues(start = dimens.smallPadding, end = dimens.smallPadding, bottom = dimens.smallPadding)
        ) {
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.primary)
                        .padding(top = dimens.smallPadding),
                    horizontalArrangement = Arrangement.spacedBy(dimens.smallPadding),
                ) {
                    SectionHeaderQuoteInfo(
                        modifier = Modifier
                            .weight(1f),
                        startText = stringResource(id = R.string.size),
                        endText = stringResource(id = R.string.bid),
                    )
                    SectionHeaderQuoteInfo(
                        modifier = Modifier
                            .weight(1f),
                        startText = stringResource(id = R.string.ask),
                        endText = stringResource(id = R.string.size),
                    )
                }
            }
            items(
                items = quotes,
                key = { it.id }
            ) { quote ->
                QuoteItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimens.spaceBetweenItemsInfoAsset),
                    quote = quote,
                )
            }
            item {
                if(quotes.isEmpty())
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.not_available_data),
                        textAlign = TextAlign.Center,
                    )
            }
        }
}


@Composable
fun SectionHeaderQuoteInfo(
    modifier: Modifier,
    startText: String,
    endText: String,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TitleHeaderQuoteInfo(
            text = startText,
        )
        TitleHeaderQuoteInfo(
            text = endText,
        )
    }
}

@Composable
fun TitleHeaderQuoteInfo(
    text: String,
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun QuoteItem(
    modifier: Modifier,
    quote: QuoteAsset,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimens.smallPadding),
    ) {
        QuoteInfoBidAskInfo(
            modifier = Modifier
                .weight(1f),
            startText = quote.bidSize.toString(),
            endText = quote.bidPrice.toString(),
            colorTextEnd = Green,
        )
        QuoteInfoBidAskInfo(
            modifier = Modifier
                .weight(1f),
            startText = quote.askPrice.toString(),
            endText = quote.askSize.toString(),
            colorTextStart = Red,
        )
    }
}

@Composable
fun QuoteInfoBidAskInfo(
    modifier: Modifier,
    startText: String,
    endText: String,
    colorTextStart: Color? = null,
    colorTextEnd: Color? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = startText,
            color = colorTextStart ?: MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text = endText,
            color = colorTextEnd ?: MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}