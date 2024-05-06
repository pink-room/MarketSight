package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api

import com.google.gson.annotations.SerializedName

data class TradeAssetDto(
    @SerializedName("T") val type: String? = null,
    @SerializedName("i") val tradeId: Long,
    @SerializedName("p") val tradePrice: Double,
    @SerializedName("t") val dateTransaction: String,
    @SerializedName("S") val symbol: String? = null,
)
