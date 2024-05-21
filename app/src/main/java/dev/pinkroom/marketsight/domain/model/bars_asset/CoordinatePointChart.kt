package dev.pinkroom.marketsight.domain.model.bars_asset

import android.graphics.PointF
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class CoordinatePointChart(
    val coordinates: PointF,
    val closingPrice: Double,
    val timestamp: LocalDateTime,
): Parcelable
