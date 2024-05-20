package dev.pinkroom.marketsight.domain.model.bars_asset

import androidx.annotation.StringRes
import dev.pinkroom.marketsight.domain.model.common.DateTimeUnit
import java.time.LocalDateTime

data class FilterHistoricalBar(
    val value: Int,
    @StringRes val timeFrameString: Int,
    val timeFrameIntervalValues: TimeFrame,
    val dateTimeUnit: DateTimeUnit,
) {
    fun getStarLocalDateTime(): LocalDateTime {
        val actualDate = LocalDateTime.now()
        return when(dateTimeUnit) {
            DateTimeUnit.All -> actualDate.minusYears(value.toLong())
            DateTimeUnit.Day -> actualDate.minusDays(value.toLong())
            DateTimeUnit.Hour -> actualDate.minusHours(value.toLong())
            DateTimeUnit.Month -> actualDate.minusMonths(value.toLong())
            DateTimeUnit.Year -> actualDate.minusYears(value.toLong())
        }
    }

    fun getEndLocalDateTime(): LocalDateTime = LocalDateTime.now()

    fun getValueTimeFrame() = when(dateTimeUnit) {
        DateTimeUnit.All -> ""
        else -> value.toString()
    }
}