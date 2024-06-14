package dev.pinkroom.marketsight.domain.model.bars_asset

data class CurrentPriceInfo(
    val price: Double = 0.0,
    val fiatCurrency: String = "$",
    val percentage: Double? = null,
    val priceDifference: Double? = null,
)
