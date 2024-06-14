package dev.pinkroom.marketsight.domain.use_case.market

import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.repository.AssetsRepository
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetStatusServiceAssetUseCase @Inject constructor(
    private val assetsRepository: AssetsRepository,
    private val dispatcher: DispatcherProvider,
) {
    operator fun invoke(
        typeAsset: TypeAsset,
    ) = assetsRepository.statusService(
        typeAsset = typeAsset,
    ).flowOn(dispatcher.IO)
}