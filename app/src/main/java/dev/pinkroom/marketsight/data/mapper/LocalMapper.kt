package dev.pinkroom.marketsight.data.mapper

import dev.pinkroom.marketsight.data.local.entity.AssetEntity
import dev.pinkroom.marketsight.domain.model.assets.Asset

fun AssetEntity.toAsset() = Asset(
    id = id,
    name = name,
    symbol = symbol,
    exchange = exchange,
    isStock = type != "crypto",
)