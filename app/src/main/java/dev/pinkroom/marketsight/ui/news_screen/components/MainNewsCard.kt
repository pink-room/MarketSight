package dev.pinkroom.marketsight.ui.news_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import coil.compose.SubcomposeAsyncImage
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.domain.model.news.ImageSize
import dev.pinkroom.marketsight.domain.model.news.ImagesNews
import dev.pinkroom.marketsight.domain.model.news.NewsInfo
import dev.pinkroom.marketsight.domain.model.news.getAspectRatio
import dev.pinkroom.marketsight.ui.core.theme.Black
import dev.pinkroom.marketsight.ui.core.theme.White
import dev.pinkroom.marketsight.ui.core.theme.dimens
import dev.pinkroom.marketsight.ui.core.theme.shimmerEffect
import java.time.LocalDateTime

@Composable
fun MainNewsCard(
    modifier: Modifier = Modifier,
    news: NewsInfo,
    onClick: (news: NewsInfo) -> Unit
){
    var size by remember { mutableStateOf(IntSize.Zero) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = dimens.normalElevation,
                shape = RoundedCornerShape(size = dimens.normalShape)
            )
            .aspectRatio(ratio = dimens.imageSizeMainNews.getAspectRatio())
            .onSizeChanged {
                size = it
            }
            .clickable(
                onClick = { onClick(news) }
            ),
    ) {
        SubcomposeAsyncImage(
            model = news.getImageUrl(imageSize = dimens.imageSizeMainNews),
            contentDescription = null,
            error = {
                val imageToLoad = when (dimens.imageSizeMainNews) {
                    ImageSize.Large -> R.drawable.default_news_large
                    ImageSize.Small -> R.drawable.default_news_small
                    ImageSize.Thumb -> R.drawable.default_news_thumb
                }
                Image(
                    painter = painterResource(id = imageToLoad),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                )
            },
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shimmerEffect()
                )
            },
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Black,
                        ),
                        startY = 0f,
                        endY = size.height.toFloat() / 1.35f
                    )
                )
        ){
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = dimens.normalPadding)
                    .padding(bottom = dimens.normalPadding),
                verticalArrangement = Arrangement.spacedBy(dimens.xSmallPadding)
            ) {
                Text(
                    text = news.headline,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = news.getUpdatedDateFormatted(),
                        color = White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                    )
                    Text(
                        modifier = Modifier.weight(0.8f),
                        textAlign = TextAlign.End,
                        text = news.getAllSymbols(),
                        fontWeight = FontWeight.Bold,
                        color = White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun MainNewsCardPreview(){
    MainNewsCard(
        news = NewsInfo(
            id = 1L,
            symbols = listOf("TSLA","AAPL","BTC"),
            images = listOf(
                ImagesNews(
                    size = ImageSize.Small,
                    url = "https://cdn.benzinga.com/files/imagecache/1024x768xUP/market-clubhouse-morning-memo_201.png",
                )
            ),
            source = "Bezinga",
            url = "https://www.benzinga.com/",
            updatedAt = LocalDateTime.now(),
            createdAt = LocalDateTime.now(),
            author = "RIPS",
            summary = "Good Morning Traders! In today&#39;s Market Clubhouse Morning Memo, we will discuss SPY, QQQ, AAPL, MSFT, NVDA, GOOGL, META, and TSLA.",
            headline = "Market Clubhouse Morning Memo - April 11th, 2024 (Trade Strategy For SPY, QQQ, AAPL, MSFT, NVDA, GOOGL, META, And TSLA)",
        ),
        onClick = {}
    )
}