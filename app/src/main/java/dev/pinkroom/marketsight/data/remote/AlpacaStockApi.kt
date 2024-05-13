package dev.pinkroom.marketsight.data.remote

import dev.pinkroom.marketsight.common.Constants.DEFAULT_LIMIT_ASSET
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.BarsResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.QuotesResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.TradesResponseDto
import dev.pinkroom.marketsight.domain.model.bars_asset.TimeFrame
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AlpacaStockApi {
    @GET("{symbol}/bars")
    suspend fun getHistoricalBarsStock(
        @Path("symbol") symbol: String,
        @Query("timeframe") timeFrame: String? = TimeFrame.Day.frameValue,
        @Query("limit") limit: Int? = DEFAULT_LIMIT_ASSET,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null,
        @Query("feed") feed: String? = "iex",
        @Query("sort") sort: String? = null,
    ): BarsResponseDto

    @GET("{symbol}/trades")
    suspend fun getHistoricalTradesStock(
        @Path("symbol") symbol: String,
        @Query("limit") limit: Int? = DEFAULT_LIMIT_ASSET,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null,
        @Query("feed") feed: String? = "iex",
        @Query("sort") sort: String? = null,
        @Query("page_token") pageToken: String? = null,
    ): TradesResponseDto

    @GET("{symbol}/quotes")
    suspend fun getHistoricalQuotesStock(
        @Path("symbol") symbol: String,
        @Query("limit") limit: Int? = DEFAULT_LIMIT_ASSET,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null,
        @Query("feed") feed: String? = "iex",
        @Query("sort") sort: String? = null,
        @Query("page_token") pageToken: String? = null,
    ): QuotesResponseDto
}