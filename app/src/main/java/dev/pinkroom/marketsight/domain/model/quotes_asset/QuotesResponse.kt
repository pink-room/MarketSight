package dev.pinkroom.marketsight.domain.model.quotes_asset

data class QuotesResponse(
    val quotes: List<QuoteAsset>,
    val pageToken: String? = null,
)
