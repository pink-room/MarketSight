package dev.pinkroom.marketsight.domain.model.trades_asset

import java.time.LocalDateTime

data class TradeAsset(
    val id: Long,
    val tradePrice: Double,
    val timeStamp: LocalDateTime,
    val symbol: String? = null,
)
