package dev.pinkroom.marketsight.data.repository

import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.data.data_source.AssetsRemoteDataSource
import dev.pinkroom.marketsight.data.mapper.toAsset
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.repository.AssetsRepository
import javax.inject.Inject

class AssetsRepositoryImp @Inject constructor(
    private val assetsRemoteDataSource: AssetsRemoteDataSource,
): AssetsRepository {
    override suspend fun getAllAssets(typeAsset: TypeAsset): Resource<List<Asset>> {
        return try {
            val response = assetsRemoteDataSource.getAllAssets(
                typeAsset = typeAsset,
            )
            Resource.Success(data = response.map { it.toAsset() })
        } catch (e: Exception){
            e.printStackTrace()
            Resource.Error(message = e.message ?: "Something Went Wrong")
        }
    }
}