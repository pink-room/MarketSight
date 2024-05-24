package dev.pinkroom.marketsight.domain.model.quotes_asset

import java.time.LocalDateTime
import java.util.UUID

data class QuoteAsset(
    val id: String = UUID.randomUUID().toString(),
    val bidPrice: Double,
    val bidSize: Double,
    val askPrice: Double,
    val askSize: Double,
    val timeStamp: LocalDateTime,
    val symbol: String? = null,
)
