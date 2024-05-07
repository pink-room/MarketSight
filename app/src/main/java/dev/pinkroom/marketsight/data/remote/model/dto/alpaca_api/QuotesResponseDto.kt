package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api

import com.google.gson.annotations.SerializedName

data class QuotesResponseDto(
    val quotes: List<QuoteAssetDto>,
    @SerializedName("next_page_token") val pageToken: String? = null,
    val symbol: String,
)
