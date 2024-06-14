package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api

import com.google.gson.annotations.SerializedName

data class BarsResponseDto(
    val bars: List<BarAssetDto>? = null,
    @SerializedName("next_page_token") val nextPageToken: String? = null,
    val symbol: String,
)
