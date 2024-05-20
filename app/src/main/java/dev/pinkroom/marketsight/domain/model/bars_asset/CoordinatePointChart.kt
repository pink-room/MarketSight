package dev.pinkroom.marketsight.domain.model.bars_asset

import android.graphics.PointF

data class CoordinatePointChart(
    val coordinates: PointF,
    val info: BarAsset,
)
