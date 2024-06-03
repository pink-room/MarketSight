package dev.pinkroom.marketsight.presentation.news_screen

import androidx.annotation.DrawableRes
import dev.pinkroom.marketsight.domain.model.news.NewsFilters
import dev.pinkroom.marketsight.domain.model.news.NewsInfo

data class NewsUiState(
    val isLoading: Boolean = true,
    val isLoadingMoreItems: Boolean = false,
    val isRefreshing: Boolean = false,
    val isToShowFilters: Boolean = false,
    @DrawableRes val errorMessage: Int? = null,
    val mainNews: List<NewsInfo> = listOf(),
    val news: List<NewsInfo> = listOf(),
    val realTimeNews: List<NewsInfo> = listOf(),
    val filters: NewsFilters = NewsFilters(),
)


