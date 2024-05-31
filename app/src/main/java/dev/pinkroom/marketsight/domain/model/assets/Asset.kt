package dev.pinkroom.marketsight.domain.model.assets

data class Asset(
    val id: String = "",
    val name: String = "",
    val symbol: String = "",
    val isStock: Boolean = false,
    val exchange: String = "",
) {
    fun getTypeAsset() = if (isStock) TypeAsset.Stock else TypeAsset.Crypto
}
