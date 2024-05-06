package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api

import com.google.gson.annotations.SerializedName

data class TradesResponseDto(
    val trades: List<TradeAssetDto>,
    @SerializedName("next_page_token") val pageToken: String? = null,
    val symbol: String,
)
