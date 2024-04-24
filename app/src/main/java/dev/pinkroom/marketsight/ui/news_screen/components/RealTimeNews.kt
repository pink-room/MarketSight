package dev.pinkroom.marketsight.ui.news_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.domain.model.news.NewsInfo
import dev.pinkroom.marketsight.ui.core.theme.Red
import dev.pinkroom.marketsight.ui.core.theme.dimens

@Composable
fun RealTimeNews(
    modifier: Modifier = Modifier,
    news: List<NewsInfo>,
    isLoading: Boolean,
    onNewsClick: (news: NewsInfo) -> Unit,
){
    var liveUpdate by rememberSaveable {
        mutableStateOf(false)
    }
    val sizeLiveIcon by animateFloatAsState(
        targetValue = if (liveUpdate) 1.3f else 1f,
        animationSpec = spring(
            stiffness = Spring.StiffnessMedium,
            dampingRatio = Spring.DampingRatioHighBouncy,
        ),
        finishedListener = {
            liveUpdate = false
        },
        label = "Animation Live Circle"
    )
    LaunchedEffect(news.size) {
        liveUpdate = true
    }

    AnimatedVisibility(
        visible = news.isNotEmpty() && !isLoading,
        enter = slideInHorizontally() + fadeIn()
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(dimens.smallPadding),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.smallPadding),
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = dimens.horizontalPadding),
                    text = stringResource(id = R.string.real_time),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Box(
                    modifier = Modifier
                        .size(dimens.smallIconSize)
                        .scale(sizeLiveIcon)
                        .background(color = Red, shape = CircleShape)
                )
            }
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