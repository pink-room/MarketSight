package dev.pinkroom.marketsight.data.data_source

import dev.pinkroom.marketsight.data.remote.AlpacaPaperApi
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_paper_api.AssetDto
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import javax.inject.Inject

class AssetsRemoteDataSource @Inject constructor(
    private val alpacaPaperApi: AlpacaPaperApi,
) {
    suspend fun getAllAssets(typeAsset: TypeAsset): List<AssetDto> {
        return alpacaPaperApi.getAssets(typeAsset = typeAsset.value)
    }

    suspend fun getAssetById(id: String): AssetDto {
        return alpacaPaperApi.getAssetById(id = id)
    }
}