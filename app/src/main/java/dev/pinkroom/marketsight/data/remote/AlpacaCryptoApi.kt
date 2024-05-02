package dev.pinkroom.marketsight.data.remote

import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_crypto_api.BarsCryptoResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_crypto_api.HistoricalQuotesCryptoResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_crypto_api.HistoricalTradeCryptoResponseDto
import dev.pinkroom.marketsight.domain.model.historical_bars.TimeFrame
import retrofit2.http.GET
import retrofit2.http.Query

interface AlpacaCryptoApi {
    @GET("bars")
    suspend fun getHistoricalBarsCrypto(
        @Query("symbols") symbol: String,
        @Query("timeframe") timeFrame: String? = TimeFrame.Day.frameValue,
        @Query("limit") limit: Int? = 1000,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null,
        @Query("sort") sort: String? = null,
    ): BarsCryptoResponseDto

    @GET("trades")
    suspend fun getHistoricalTradesCrypto(
        @Query("symbol") symbol: String,
        @Query("limit") limit: Int? = 1000,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null,
        @Query("sort") sort: String? = null,
        @Query("page_token") pageToken: String? = null,
    ): HistoricalTradeCryptoResponseDto

    @GET("quotes")
    suspend fun getHistoricalQuotesCrypto(
        @Query("symbol") symbol: String,
        @Query("limit") limit: Int? = 1000,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null,
        @Query("sort") sort: String? = null,
        @Query("page_token") pageToken: String? = null,
    ): HistoricalQuotesCryptoResponseDto
}