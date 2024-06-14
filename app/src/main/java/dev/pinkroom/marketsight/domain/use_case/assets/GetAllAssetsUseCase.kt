package dev.pinkroom.marketsight.domain.use_case.assets

import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.repository.AssetsRepository
import javax.inject.Inject

class GetAllAssetsUseCase @Inject constructor(
    private val assetsRepository: AssetsRepository,
){
    suspend operator fun invoke(
        typeAsset: TypeAsset,
        fetchFromRemote: Boolean = false,
    ) = assetsRepository.getAllAssets(typeAsset = typeAsset, fetchFromRemote = fetchFromRemote)
}