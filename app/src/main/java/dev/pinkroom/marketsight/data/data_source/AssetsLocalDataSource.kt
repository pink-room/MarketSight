package dev.pinkroom.marketsight.data.data_source

import dev.pinkroom.marketsight.data.local.DbTransaction
import dev.pinkroom.marketsight.data.local.dao.AssetDao
import dev.pinkroom.marketsight.data.local.entity.AssetEntity
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import javax.inject.Inject

class AssetsLocalDataSource  @Inject constructor(
    private val dbTransaction: DbTransaction,
    private val assetDao: AssetDao,
) {
    suspend fun cacheAssets(data: List<AssetEntity>, typeAsset: TypeAsset) = dbTransaction {
        deleteAllAssetsOfType(typeAsset = typeAsset)
        assetDao.insert(assets = data)
        getAllAssetsOfType(typeAsset = typeAsset)
    }

    suspend fun deleteAllAssetsOfType(typeAsset: TypeAsset) = assetDao.clearAllOfType(typeAsset = typeAsset.value)

    suspend fun getAllAssetsOfType(typeAsset: TypeAsset) = assetDao.getAllAssetsOfType(typeAsset = typeAsset.value)
}