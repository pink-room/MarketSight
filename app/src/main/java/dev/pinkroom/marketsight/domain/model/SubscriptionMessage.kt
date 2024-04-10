package dev.pinkroom.marketsight.domain.model

data class SubscriptionMessage(
    val news: List<String>? = null,
    val quotes: List<String>? = null,
    val trades: List<String>? = null,
)
