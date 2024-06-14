package dev.pinkroom.marketsight.domain.model.assets

sealed interface TypeAssetDetailFilter {
    data object Quotes: TypeAssetDetailFilter
    data object Trades: TypeAssetDetailFilter
}