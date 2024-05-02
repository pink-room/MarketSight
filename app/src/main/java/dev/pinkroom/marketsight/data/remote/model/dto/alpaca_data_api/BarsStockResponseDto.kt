package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_data_api

import com.google.gson.annotations.SerializedName

data class BarsStockResponseDto(
    val bars: List<BarAssetDto>,
    @SerializedName("next_page_token") val nextPageToken: String? = null,
    val symbol: String,
)
