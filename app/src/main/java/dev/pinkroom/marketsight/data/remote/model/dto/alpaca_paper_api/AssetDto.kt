package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_paper_api

import com.google.gson.annotations.SerializedName

data class AssetDto(
    val id: String,
    @SerializedName("class") val type: String,
    val symbol: String,
    val name: String,
    val exchange: String,
)
