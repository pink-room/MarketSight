package dev.pinkroom.marketsight.domain.use_case.assets

import dev.pinkroom.marketsight.domain.repository.AssetsRepository
import javax.inject.Inject

class GetAssetById @Inject constructor(
    private val assetsRepository: AssetsRepository,
){
    suspend operator fun invoke(
        id: String,
    ) = assetsRepository.getAssetById(id = id)
}