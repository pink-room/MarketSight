package dev.pinkroom.marketsight.domain.model.bars_asset

import java.time.LocalDateTime

data class BarAsset(
    val closingPrice: Double = 0.0,
    val highPrice: Double = 0.0,
    val lowPrice: Double = 0.0,
    val tradeCountInBar: Int = 0,
    val openingPrice: Double = 0.0,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val barVolume: Double = 0.0,
    val volumeWeightedAvgPrice: Double = 0.0,
    val symbol: String? = null,
)
