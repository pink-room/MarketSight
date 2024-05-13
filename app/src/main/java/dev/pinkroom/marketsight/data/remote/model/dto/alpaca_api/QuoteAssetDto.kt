package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api

import com.google.gson.annotations.SerializedName

data class QuoteAssetDto(
    @SerializedName("T") val type: String? = null,
    @SerializedName("i") val tradeId: Long,
    @SerializedName("bp") val bidPrice: Double,
    @SerializedName("ap") val askPrice: Double,
    @SerializedName("t") val requestDate: String,
    @SerializedName("S") val symbol: String? = null,
)