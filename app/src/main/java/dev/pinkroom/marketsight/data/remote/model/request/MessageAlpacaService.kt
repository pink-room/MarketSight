package dev.pinkroom.marketsight.data.remote.model.request

data class MessageAlpacaService(
    val action: String, // subscribe / unsubscribe
    val trades: List<String> = emptyList(),
    val quotes: List<String> = emptyList(),
    val news: List<String> = emptyList(),
)
