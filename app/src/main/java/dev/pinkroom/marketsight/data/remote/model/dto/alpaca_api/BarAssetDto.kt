package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api

import com.google.gson.annotations.SerializedName

data class BarAssetDto(
    @SerializedName("c") val closingPrice: Double,
    @SerializedName("h") val highPrice: Double,
    @SerializedName("l") val lowPrice: Double,
    @SerializedName("n") val tradeCountInBar: Int,
    @SerializedName("o") val openingPrice: Double,
    @SerializedName("t") val timestamp: String,
    @SerializedName("v") val barVolume: Double,
    @SerializedName("vw") val volumeWeightedAvgPrice: Double,
    @SerializedName("S") val symbol: String? = null,
)
