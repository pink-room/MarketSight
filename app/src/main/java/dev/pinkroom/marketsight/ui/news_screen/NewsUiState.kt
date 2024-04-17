package dev.pinkroom.marketsight.ui.news_screen

import dev.pinkroom.marketsight.domain.model.common.SubInfoSymbols
import dev.pinkroom.marketsight.domain.model.news.ImageSize
import dev.pinkroom.marketsight.domain.model.news.ImagesNews
import dev.pinkroom.marketsight.domain.model.news.NewsInfo
import java.time.LocalDateTime

data class NewsUiState(
    val isLoading: Boolean = false,
    val news: List<NewsInfo> = listOf(
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
                    url = "https://cdn.benzinga.com/files/imagecache/1024x768xUP/market-clubhouse-morning-memo_201.p",
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
    val realTimeNews: List<NewsInfo> = listOf(),
    val symbols: List<SubInfoSymbols> = listOf(),
)
