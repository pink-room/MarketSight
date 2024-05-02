package dev.pinkroom.marketsight.domain.repository

import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.domain.model.historical_bars.BarAsset
import dev.pinkroom.marketsight.domain.model.historical_bars.TimeFrame

interface StockRepository {
    suspend fun getHistoricalBars(
        symbol: String,
        timeFrame: TimeFrame? = TimeFrame.Day,
        limit: Int? = 1000,
        startDate: String,
        endDate: String,
        sort: SortType? = SortType.ASC,
    ): Resource<List<BarAsset>>
}