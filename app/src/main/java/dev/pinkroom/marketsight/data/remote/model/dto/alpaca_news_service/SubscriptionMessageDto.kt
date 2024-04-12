package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service

import com.google.gson.annotations.SerializedName

data class SubscriptionMessageDto(
    @SerializedName("T") val type: String,
    val news: List<String>? = null,
    val quotes: List<String>? = null,
    val trades: List<String>? = null,
)