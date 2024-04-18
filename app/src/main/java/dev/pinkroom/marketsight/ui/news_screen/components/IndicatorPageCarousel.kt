package dev.pinkroom.marketsight.ui.news_screen.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.pinkroom.marketsight.common.Constants
import dev.pinkroom.marketsight.ui.core.theme.PhilippineGray
import dev.pinkroom.marketsight.ui.core.theme.PhilippineSilver
import dev.pinkroom.marketsight.ui.core.theme.dimens
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IndicatorPageCarousel(
    pagerState: PagerState,
    changePage: (currentPage: Int) -> Unit,
){
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val selectedColor = if (isSystemInDarkTheme()) PhilippineSilver else PhilippineGray
            val unSelectedColor = if (isSystemInDarkTheme()) PhilippineGray else PhilippineSilver
            val color = if (pagerState.currentPage == iteration) selectedColor else unSelectedColor

            Box(
                modifier = Modifier
                    .padding(horizontal = dimens.spaceBetweenPageIndicator)
                    .clip(CircleShape)
                    .background(color)
                    .size(dimens.circlePageIndicatorSize)
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource,
                        onClick = {
                            if (pagerState.currentPage != iteration) {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        page = iteration,
                                        animationSpec = tween(
                                            durationMillis = Constants.ANIM_TIME_CAROUSEL
                                        )
                                    )
                                }
                                changePage(iteration)
                            }
                        }
                    ),
            )
        }
    }
}