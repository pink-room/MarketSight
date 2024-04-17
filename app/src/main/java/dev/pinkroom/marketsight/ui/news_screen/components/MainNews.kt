package dev.pinkroom.marketsight.ui.news_screen.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.pinkroom.marketsight.domain.model.news.NewsInfo
import dev.pinkroom.marketsight.ui.core.theme.dimens

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainNews(
    modifier: Modifier = Modifier,
    newsList: List<NewsInfo>,
    onNewsClick: (news: NewsInfo) -> Unit,
){
    val pagerState = rememberPagerState(
        pageCount = { newsList.size }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        HorizontalPager(
            state = pagerState,
            key = { newsList[it].id }
        ) { index ->
            val news = newsList[index]
            MainNewsCard(
                modifier = modifier,
                news = news,
                onClick = onNewsClick
            )
        }
        Spacer(modifier = Modifier.height(dimens.smallPadding))
        IndicatorPageCarousel(pagerState = pagerState)
    }
}