package dev.pinkroom.marketsight.domain.repository

import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset

interface AssetsRepository {
    suspend fun getAllAssets(
        typeAsset: TypeAsset
    ): Resource<List<Asset>>
}