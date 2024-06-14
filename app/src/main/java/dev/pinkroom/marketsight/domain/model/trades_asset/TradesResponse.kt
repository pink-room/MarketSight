package dev.pinkroom.marketsight.domain.model.trades_asset

data class TradesResponse(
    val trades: List<TradeAsset>,
    val pageToken: String? = null,
)
