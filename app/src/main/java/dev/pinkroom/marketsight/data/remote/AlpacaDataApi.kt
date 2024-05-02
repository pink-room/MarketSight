package dev.pinkroom.marketsight.data.remote

import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_data_api.BarsCryptoResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_data_api.BarsStockResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_data_api.NewsResponseDto
import dev.pinkroom.marketsight.domain.model.historical_bars.TimeFrame
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AlpacaDataApi {
    @GET("v1beta1/news")
    suspend fun getNews(
        @Query("symbols") symbols: String? = null,
        @Query("limit") perPage: Int? = null,
        @Query("page_token") pageToken: String? = null,
        @Query("sort") sort: String? = null,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null,
    ): NewsResponseDto

    @GET("v2/stocks/{symbol}/bars")
    suspend fun getHistoricalBarsStock(
        @Path("symbol") symbol: String,
        @Query("timeframe") timeFrame: String? = TimeFrame.Day.frameValue,
        @Query("limit") limit: Int? = 1000,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null,
        @Query("feed") feed: String? = "iex",
        @Query("sort") sort: String? = null,
    ): BarsStockResponseDto

    @GET("v1beta3/crypto/us/bars")
    suspend fun getHistoricalBarsCrypto(
        @Query("symbols") symbol: String,
        @Query("timeframe") timeFrame: String? = TimeFrame.Day.frameValue,
        @Query("limit") limit: Int? = 1000,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null,
        @Query("sort") sort: String? = null,
    ): BarsCryptoResponseDto
}