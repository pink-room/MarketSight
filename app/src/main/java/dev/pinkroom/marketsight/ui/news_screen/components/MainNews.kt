package dev.pinkroom.marketsight.ui.news_screen.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import dev.pinkroom.marketsight.common.Constants
import dev.pinkroom.marketsight.domain.model.news.NewsInfo
import dev.pinkroom.marketsight.domain.model.news.getAspectRatio
import dev.pinkroom.marketsight.ui.core.theme.dimens
import dev.pinkroom.marketsight.ui.core.theme.shimmerEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainNews(
    modifier: Modifier = Modifier,
    newsList: List<NewsInfo>,
    isLoading: Boolean,
    onNewsClick: (news: NewsInfo) -> Unit,
    autoScrollDuration: Long = 5000L,
){
    val pagerState = rememberPagerState(
        pageCount = { newsList.size }
    )

    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    var currentPageKey by remember { mutableIntStateOf(pagerState.currentPage) }
    if (isDragged.not() && newsList.isNotEmpty() && !isLoading) {
        with(pagerState) {
            LaunchedEffect(key1 = currentPageKey) {
                launch {
                    delay(timeMillis = autoScrollDuration)
                    val nextPage = (currentPage + 1).mod(pageCount)
                    animateScrollToPage(
                        page = nextPage,
                        animationSpec = tween(
                            durationMillis = Constants.ANIM_TIME_CAROUSEL
                        )
                    )
                    currentPageKey = nextPage
                }
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.horizontalPadding)
                .shadow(
                    elevation = dimens.normalElevation,
                    shape = RoundedCornerShape(size = dimens.normalShape)
                )
                .aspectRatio(dimens.imageSizeMainNews.getAspectRatio())
                .shimmerEffect()
        )
    } else if (newsList.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            HorizontalPager(
                state = pagerState,
                key = { newsList[it].id },
                contentPadding = PaddingValues(horizontal = dimens.horizontalPadding),
                pageSpacing = dimens.normalPadding
            ) { index ->
                val news = newsList[index]
                MainNewsCard(
                    modifier = modifier
                        .carouselTransition(index, pagerState),
                    news = news,
                    onClick = onNewsClick
                )
            }
            Spacer(modifier = Modifier.height(dimens.smallPadding))
            IndicatorPageCarousel(
                pagerState = pagerState,
                changePage = {
                    currentPageKey = it
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.carouselTransition(page: Int, pagerState: PagerState) =
    graphicsLayer {
        val pageOffset =
            ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

        val transformation = lerp(
            start = 0.7f,
            stop = 1f,
            fraction = 1f - pageOffset.coerceIn(0f, 1f)
        )
        alpha = transformation
        scaleY = transformation
    }