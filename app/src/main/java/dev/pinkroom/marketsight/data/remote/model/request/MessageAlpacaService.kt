package dev.pinkroom.marketsight.data.remote.model.request

data class MessageAlpacaService(
    val action: String, // subscribe / unsubscribe
    val trades: List<String>? = null,
    val quotes: List<String>? = null,
    val bars: List<String>? = null,
    val news: List<String>? = null,
)
