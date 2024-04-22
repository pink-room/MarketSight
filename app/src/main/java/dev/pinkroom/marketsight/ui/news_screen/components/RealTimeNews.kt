package dev.pinkroom.marketsight.ui.news_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.domain.model.news.NewsInfo
import dev.pinkroom.marketsight.ui.core.theme.dimens

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RealTimeNews(
    modifier: Modifier = Modifier,
    news: List<NewsInfo>,
    isLoading: Boolean,
    onNewsClick: (news: NewsInfo) -> Unit,
){
    AnimatedVisibility(
        visible = news.isNotEmpty() && !isLoading,
        enter = slideInHorizontally() + fadeIn()
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(dimens.smallPadding)
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = dimens.horizontalPadding),
                text = stringResource(id = R.string.real_time),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = dimens.horizontalPadding,
                ),
            ) {
                items(
                    items = news,
                ){
                    RealTimeNewsCard(
                        modifier = Modifier
                            .width(dimens.liveNewsCardWidth)
                            .height(dimens.liveNewsCardHeight)
                            .padding(vertical = dimens.smallPadding)
                            .padding(end = dimens.normalPadding),
                        news = it,
                        onClick = onNewsClick
                    )
                }
            }
        }
    }
    
}