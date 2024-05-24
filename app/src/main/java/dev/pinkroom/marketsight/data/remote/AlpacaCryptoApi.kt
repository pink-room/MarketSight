package dev.pinkroom.marketsight.data.remote

import dev.pinkroom.marketsight.common.Constants.DEFAULT_LIMIT_ASSET
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_crypto_api.BarsCryptoResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_crypto_api.LatestBarCryptoResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_crypto_api.QuotesCryptoResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_crypto_api.TradesCryptoResponseDto
import dev.pinkroom.marketsight.domain.model.bars_asset.TimeFrame
import retrofit2.http.GET
import retrofit2.http.Query

interface AlpacaCryptoApi {
    @GET("bars")
    suspend fun getHistoricalBarsCrypto(
        @Query("symbols") symbol: String,
        @Query("timeframe") timeFrame: String? = TimeFrame.Day.frameValue,
        @Query("limit") limit: Int? = DEFAULT_LIMIT_ASSET,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null,
        @Query("sort") sort: String? = null,
    ): BarsCryptoResponseDto

    @GET("latest/bars")
    suspend fun getLatestBarCrypto(
        @Query("symbols") symbol: String,
    ): LatestBarCryptoResponseDto

    @GET("trades")
    suspend fun getHistoricalTradesCrypto(
        @Query("symbols") symbol: String,
        @Query("limit") limit: Int? = DEFAULT_LIMIT_ASSET,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null,
        @Query("sort") sort: String? = null,
        @Query("page_token") pageToken: String? = null,
    ): TradesCryptoResponseDto

    @GET("quotes")
    suspend fun getHistoricalQuotesCrypto(
        @Query("symbols") symbol: String,
        @Query("limit") limit: Int? = DEFAULT_LIMIT_ASSET,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null,
        @Query("sort") sort: String? = null,
        @Query("page_token") pageToken: String? = null,
    ): QuotesCryptoResponseDto
}