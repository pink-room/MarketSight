package dev.pinkroom.marketsight.domain.use_case.market

import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.repository.AssetsRepository
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetRealTimeTradesAssetUseCase @Inject constructor(
    private val assetsRepository: AssetsRepository,
    private val dispatchers: DispatcherProvider,
) {
    operator fun invoke(
        symbol: String,
        typeAsset: TypeAsset,
    ) = assetsRepository.getRealTimeTrades(
        symbol = symbol,
        typeAsset = typeAsset,
    ).flowOn(dispatchers.IO)
}