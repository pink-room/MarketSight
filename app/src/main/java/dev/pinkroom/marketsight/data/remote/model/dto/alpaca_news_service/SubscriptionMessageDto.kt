package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service

data class SubscriptionMessageDto(
    val news: List<String>? = null,
    val quotes: List<String>? = null,
    val trades: List<String>? = null,
)