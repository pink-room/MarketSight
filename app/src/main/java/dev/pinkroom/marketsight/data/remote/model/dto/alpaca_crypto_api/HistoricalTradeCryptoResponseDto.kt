package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_crypto_api

import com.google.gson.annotations.SerializedName
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.TradeAssetDto

data class HistoricalTradeCryptoResponseDto(
    val trades: Map<String, List<TradeAssetDto>>,
    @SerializedName("next_page_token") val pageToken: String? = null,
)
