package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service

import com.google.gson.annotations.SerializedName


data class NewsMessageDto(
    val id: Long,
    val headline: String,
    val summary: String,
    val author: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    val url: String,
    val symbols: List<String> = emptyList(),
    val source: String,
)
