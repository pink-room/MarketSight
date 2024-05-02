package dev.pinkroom.marketsight.domain.model.historical_bars

import java.time.LocalDateTime

data class BarAsset(
    val closingPrice: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val tradeCountInBar: Int,
    val openingPrice: Double,
    val timestamp: LocalDateTime,
    val barVolume: Double,
    val volumeWeightedAvgPrice: Double,
)
