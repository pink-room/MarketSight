package dev.pinkroom.marketsight.ui.news_screen

sealed class NewsEvent {
    data object RetryNews: NewsEvent()
    data object RefreshNews: NewsEvent()
}
