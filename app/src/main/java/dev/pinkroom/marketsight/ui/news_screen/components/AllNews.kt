package dev.pinkroom.marketsight.ui.news_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.domain.model.news.NewsInfo
import dev.pinkroom.marketsight.ui.core.theme.dimens
import dev.pinkroom.marketsight.ui.core.theme.shimmerEffect

fun LazyListScope.AllNews(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    news: List<NewsInfo>,
    navigateToNews: (newsInfo: NewsInfo) -> Unit,
){
    if (isLoading)
        items(3){
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(dimens.newsCard)
                    .padding(horizontal = dimens.horizontalPadding, vertical = dimens.smallPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = dimens.normalElevation,
                            shape = RoundedCornerShape(size = dimens.normalShape)
                        )
                        .weight(0.5f)
                        .fillMaxSize()
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = dimens.smallPadding)
                        .fillMaxHeight()
                        .shimmerEffect()
                )
            }
        }
    else {
        item {
            Text(
                modifier = Modifier
                    .padding(horizontal = dimens.horizontalPadding),
                text = stringResource(id = R.string.latest_news),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }
        items(news) { item ->
            AllNewsCard(
                modifier = modifier
                    .fillMaxWidth()
                    .height(dimens.newsCard)
                    .padding(horizontal = dimens.horizontalPadding, vertical = dimens.smallPadding),
                news = item,
                onNewsClick = navigateToNews
            )
        }
    }
}