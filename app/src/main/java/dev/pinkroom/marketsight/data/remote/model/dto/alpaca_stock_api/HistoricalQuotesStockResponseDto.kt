package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_stock_api

import com.google.gson.annotations.SerializedName
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.QuoteAssetDto

data class HistoricalQuotesStockResponseDto(
    val quotes: List<QuoteAssetDto>,
    @SerializedName("next_page_token") val pageToken: String? = null,
)
