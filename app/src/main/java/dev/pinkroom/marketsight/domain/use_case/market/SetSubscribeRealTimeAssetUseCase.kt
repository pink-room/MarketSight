package dev.pinkroom.marketsight.domain.use_case.market

import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.repository.AssetsRepository
import javax.inject.Inject

class SetSubscribeRealTimeAssetUseCase @Inject constructor(
    private val assetsRepository: AssetsRepository,
) {
    suspend operator fun invoke(
        symbol: String,
        typeAsset: TypeAsset,
    ) = assetsRepository.subscribeUnsubscribeRealTimeFinancialData(
        symbol = symbol,
        typeAsset = typeAsset,
        action = ActionAlpaca.Subscribe,
    )
}