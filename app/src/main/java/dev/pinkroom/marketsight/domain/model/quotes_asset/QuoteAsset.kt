package dev.pinkroom.marketsight.domain.model.quotes_asset

import java.time.LocalDateTime

data class QuoteAsset(
    val id: Long,
    val bidPrice: Double,
    val askPrice: Double,
    val timeStamp: LocalDateTime,
    val symbol: String? = null,
)
