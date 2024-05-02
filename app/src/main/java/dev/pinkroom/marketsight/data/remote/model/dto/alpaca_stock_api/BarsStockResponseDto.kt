package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_stock_api

import com.google.gson.annotations.SerializedName
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.BarAssetDto

data class BarsStockResponseDto(
    val bars: List<BarAssetDto>,
    @SerializedName("next_page_token") val nextPageToken: String? = null,
    val symbol: String,
)
