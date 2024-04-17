package dev.pinkroom.marketsight.domain.model.common

data class SubInfoSymbols(
    val name: String,
    val symbol: String,
    val isSubscribed: Boolean = false,
)
