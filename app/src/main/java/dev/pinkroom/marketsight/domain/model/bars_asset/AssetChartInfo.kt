package dev.pinkroom.marketsight.domain.model.bars_asset

data class AssetChartInfo(
    val upperValue: Double = 0.0,
    val lowerValue: Double = 0.0,
    val barsInfo: List<BarAsset> = emptyList(),
)
