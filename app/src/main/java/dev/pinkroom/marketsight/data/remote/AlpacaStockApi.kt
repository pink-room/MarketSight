package dev.pinkroom.marketsight.data.remote

import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_stock_api.BarsStockResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_stock_api.HistoricalQuotesStockResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_stock_api.HistoricalTradeStockResponseDto
import dev.pinkroom.marketsight.domain.model.historical_bars.TimeFrame
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AlpacaStockApi {
    @GET("{symbol}/bars")
    suspend fun getHistoricalBarsStock(
        @Path("symbol") symbol: String,
        @Query("timeframe") timeFrame: String? = TimeFrame.Day.frameValue,
        @Query("limit") limit: Int? = 1000,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null,
        @Query("feed") feed: String? = "iex",
        @Query("sort") sort: String? = null,
    ): BarsStockResponseDto

    @GET("{symbol}/trades")
    suspend fun getHistoricalTradesStock(
        @Path("symbol") symbol: String,
        @Query("limit") limit: Int? = 1000,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null,
        @Query("feed") feed: String? = "iex",
        @Query("sort") sort: String? = null,
        @Query("page_token") pageToken: String? = null,
    ): HistoricalTradeStockResponseDto

    @GET("{symbol}/quotes")
    suspend fun getHistoricalQuotesStock(
        @Path("symbol") symbol: String,
        @Query("limit") limit: Int? = 1000,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null,
        @Query("feed") feed: String? = "iex",
        @Query("sort") sort: String? = null,
        @Query("page_token") pageToken: String? = null,
    ): HistoricalQuotesStockResponseDto
}