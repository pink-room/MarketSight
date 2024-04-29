package dev.pinkroom.marketsight.presentation.news_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import dev.pinkroom.marketsight.domain.model.news.ImageSize
import dev.pinkroom.marketsight.domain.model.news.ImagesNews
import dev.pinkroom.marketsight.domain.model.news.NewsInfo
import dev.pinkroom.marketsight.presentation.core.theme.dimens
import java.time.LocalDateTime

@Composable
fun RealTimeNewsCard(
    modifier: Modifier = Modifier,
    news: NewsInfo,
    onClick: (news: NewsInfo) -> Unit
){
    Card(
        modifier = modifier
            .clickable { onClick(news) }
            .shadow(
                elevation = dimens.lowElevation,
                shape = RoundedCornerShape(size = dimens.normalShape)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = dimens.xSmallPadding, horizontal = dimens.smallPadding),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.Start
        ) {
            if (news.symbols.isNotEmpty())
                Text(
                    text = news.getAllSymbols(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            Text(
                text = news.headline,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = news.source,
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    text = news.getUpdatedDateFormatted(),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun RealTimeNewsCardPreview(){
    RealTimeNewsCard(
        modifier = Modifier
            .requiredWidthIn(max = dimens.liveNewsCardWidth)
            .height(dimens.liveNewsCardHeight),
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
