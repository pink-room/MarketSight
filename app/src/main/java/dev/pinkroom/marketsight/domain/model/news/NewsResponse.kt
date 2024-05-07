package dev.pinkroom.marketsight.domain.model.news

data class NewsResponse(
    val news: List<NewsInfo>,
    val nextPageToken: String? = null,
)
