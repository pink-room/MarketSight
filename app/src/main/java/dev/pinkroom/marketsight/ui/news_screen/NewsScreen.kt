package dev.pinkroom.marketsight.ui.news_screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.domain.model.common.SubInfoSymbols
import dev.pinkroom.marketsight.domain.model.news.ImageSize
import dev.pinkroom.marketsight.domain.model.news.ImagesNews
import dev.pinkroom.marketsight.domain.model.news.NewsInfo
import dev.pinkroom.marketsight.ui.core.components.PullToRefreshLazyColumn
import dev.pinkroom.marketsight.ui.core.theme.dimens
import dev.pinkroom.marketsight.ui.news_screen.components.AllNews
import dev.pinkroom.marketsight.ui.news_screen.components.EmptyNewsList
import dev.pinkroom.marketsight.ui.news_screen.components.MainNews
import dev.pinkroom.marketsight.ui.news_screen.components.RealTimeNews
import java.time.LocalDateTime

@Composable
fun NewsScreen(
    modifier: Modifier = Modifier,
    mainNews: List<NewsInfo>,
    news: List<NewsInfo>,
    realTimeNews: List<NewsInfo>,
    symbols: List<SubInfoSymbols>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    errorMessage: Int? = null,
    onEvent: (event: NewsEvent) -> Unit,
){
    val context = LocalContext.current

    PullToRefreshLazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            top = dimens.contentTopPadding,
            bottom = dimens.contentBottomPadding,
        ),
        isRefreshing = isRefreshing,
        enabledPullToRefresh = {
            !((!isLoading && errorMessage != null && news.isEmpty()) || isLoading)
        },
        onRefresh = {
            onEvent(NewsEvent.RefreshNews)
        },
    ) {
        if (!isLoading && errorMessage != null && news.isEmpty()){
            item {
                EmptyNewsList(
                    modifier = Modifier
                        .fillParentMaxSize(),
                    errorMessage = errorMessage,
                    onRetry = {
                        onEvent(NewsEvent.RetryNews)
                    },
                )
            }
        } else {
            item {
                MainNews(
                    modifier = Modifier
                        .fillMaxWidth(),
                    newsList = mainNews,
                    isLoading = isLoading,
                    onNewsClick = {
                        context.navigateToNews(newsInfo = it)
                    }
                )
            }
            item {
                RealTimeNews(
                    modifier = Modifier
                        .fillMaxWidth(),
                    news = realTimeNews,
                    isLoading = isLoading,
                    onNewsClick = {
                        context.navigateToNews(newsInfo = it)
                    }
                )
            }
            AllNews(
                news = news,
                isLoading = isLoading,
                navigateToNews = {
                    context.navigateToNews(newsInfo = it)
                }
            )
        }
    }
}

fun Context.navigateToNews(newsInfo: NewsInfo){
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsInfo.url))
    startActivity(intent)
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun NewsScreenPreview(){
    NewsScreen(
        news = listOf(
            NewsInfo(
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
            NewsInfo(
                id = 2L,
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
        ),
        mainNews = listOf(
            NewsInfo(
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
            NewsInfo(
                id = 2L,
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
        ),
        realTimeNews = listOf(
            NewsInfo(
                id = 1L,
                symbols = listOf("TSLA","AAPL","BTC"),
                images = null,
                source = "Bezinga",
                url = "https://www.benzinga.com/",
                updatedAt = LocalDateTime.now(),
                createdAt = LocalDateTime.now(),
                author = "RIPS",
                summary = "Good Morning Traders! In today&#39;s Market Clubhouse Morning Memo, we will discuss SPY, QQQ, AAPL, MSFT, NVDA, GOOGL, META, and TSLA.",
                headline = "Market Clubhouse Morning Memo - April 11th, 2024 (Trade Strategy For SPY, QQQ, AAPL, MSFT, NVDA, GOOGL, META, And TSLA)",
            ),
            NewsInfo(
                id = 2L,
                symbols = listOf("TSLA","AAPL","BTC"),
                images = null,
                source = "Bezinga",
                url = "https://www.benzinga.com/",
                updatedAt = LocalDateTime.now(),
                createdAt = LocalDateTime.now(),
                author = "RIPS",
                summary = "Good Morning Traders! In today&#39;s Market Clubhouse Morning Memo, we will discuss SPY, QQQ, AAPL, MSFT, NVDA, GOOGL, META, and TSLA.",
                headline = "Market Clubhouse Morning Memo - April 11th, 2024 (Trade Strategy For SPY, QQQ, AAPL, MSFT, NVDA, GOOGL, META, And TSLA)",
            ),
        ),
        symbols = listOf(
            SubInfoSymbols(
                name = "TESLA", symbol = "TSLA",
            ),
            SubInfoSymbols(
                name = "APPLE", symbol = "AAPL",
            ),
        ),
        isLoading = false,
        errorMessage = R.string.get_news_error_message,
        isRefreshing = false,
        onEvent = {},
    )
}