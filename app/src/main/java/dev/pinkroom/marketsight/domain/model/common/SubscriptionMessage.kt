package dev.pinkroom.marketsight.domain.model.common

data class SubscriptionMessage(
    val news: List<String>? = null,
    val quotes: List<String>? = null,
    val trades: List<String>? = null,
    val bars: List<String>? = null,
)
