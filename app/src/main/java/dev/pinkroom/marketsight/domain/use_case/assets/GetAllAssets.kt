package dev.pinkroom.marketsight.domain.use_case.assets

import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.repository.AssetsRepository
import javax.inject.Inject

class GetAllAssets @Inject constructor(
    private val assetsRepository: AssetsRepository,
){
    suspend operator fun invoke(
        typeAsset: TypeAsset,
    ) = assetsRepository.getAllAssets(typeAsset = typeAsset)
}