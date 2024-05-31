package dev.pinkroom.marketsight.domain.model.trades_asset

import java.time.LocalDateTime
import java.util.UUID

data class TradeAsset(
    val id: String = UUID.randomUUID().toString(),
    val tradeId: Long? = null,
    val tradePrice: Double,
    val tradeSize: Double,
    val timeStamp: LocalDateTime,
    val symbol: String? = null,
)
