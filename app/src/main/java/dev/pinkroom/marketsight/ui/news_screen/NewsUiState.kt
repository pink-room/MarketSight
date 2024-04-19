package dev.pinkroom.marketsight.ui.news_screen

import androidx.annotation.DrawableRes
import dev.pinkroom.marketsight.domain.model.common.SubInfoSymbols
import dev.pinkroom.marketsight.domain.model.news.NewsInfo

data class NewsUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    @DrawableRes val errorMessage: Int? = null,
    val mainNews: List<NewsInfo> = listOf(),
    val news: List<NewsInfo> = listOf(),
    val realTimeNews: List<NewsInfo> = listOf(),
    val symbols: List<SubInfoSymbols> = listOf(),
)
