package dev.pinkroom.marketsight.domain.model.assets

sealed class TypeAsset(val value: String) {
    data object Crypto: TypeAsset(value = "crypto")
    data object Stock: TypeAsset(value = "us_equity")
}