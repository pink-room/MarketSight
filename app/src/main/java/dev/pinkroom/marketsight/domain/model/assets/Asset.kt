package dev.pinkroom.marketsight.domain.model.assets

data class Asset(
    val id: String,
    val name: String,
    val symbol: String,
    val isStock: Boolean,
    val exchange: String,
)
