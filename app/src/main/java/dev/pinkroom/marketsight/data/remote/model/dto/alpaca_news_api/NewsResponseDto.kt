package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api

import com.google.gson.annotations.SerializedName

data class NewsResponseDto(
    val news: List<NewsDto>,
    @SerializedName("next_page_token") val nextPageToken: String,
)
