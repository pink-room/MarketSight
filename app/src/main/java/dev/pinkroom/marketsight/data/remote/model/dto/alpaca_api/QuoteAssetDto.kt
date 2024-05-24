package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api

import com.google.gson.annotations.SerializedName

data class QuoteAssetDto(
    @SerializedName("T") val type: String? = null,
    @SerializedName("bp") val bidPrice: Double,
    @SerializedName("bs") val bidSize: Double,
    @SerializedName("ap") val askPrice: Double,
    @SerializedName("as") val askSize: Double,
    @SerializedName("t") val requestDate: String,
    @SerializedName("S") val symbol: String? = null,
)
