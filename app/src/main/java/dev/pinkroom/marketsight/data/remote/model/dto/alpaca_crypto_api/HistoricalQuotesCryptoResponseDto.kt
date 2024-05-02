package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_crypto_api

import com.google.gson.annotations.SerializedName
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.QuoteAssetDto

data class HistoricalQuotesCryptoResponseDto(
    val quotes: Map<String,List<QuoteAssetDto>>,
    @SerializedName("next_page_token") val pageToken: String? = null,
)
