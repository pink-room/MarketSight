package dev.pinkroom.marketsight.data.remote.model.dto.request

data class MessageAlpacaServiceDto(
    val action: String, // subscribe / unsubscribe
    val trades: List<String>? = null,
    val quotes: List<String>? = null,
    val bars: List<String>? = null,
    val news: List<String>? = null,
)
