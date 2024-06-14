package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_crypto_api

import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.BarAssetDto

data class BarsCryptoResponseDto(
    val bars: Map<String, List<BarAssetDto>>,
    val nextPageToken: String? = null,
)
