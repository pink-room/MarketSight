package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_data_api

data class BarsCryptoResponseDto(
    val bars: Map<String, List<BarAssetDto>>,
    val nextPageToken: String? = null,
)
