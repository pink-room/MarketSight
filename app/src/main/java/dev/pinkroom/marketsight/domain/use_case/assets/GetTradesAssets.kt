package dev.pinkroom.marketsight.domain.use_case.assets

import dev.pinkroom.marketsight.common.Constants
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.repository.AssetsRepository
import java.time.LocalDateTime
import javax.inject.Inject

class GetTradesAssets @Inject constructor(
    private val assetsRepository: AssetsRepository,
){
    suspend operator fun invoke(
        symbol: String,
        typeAsset: TypeAsset,
        limit: Int? = Constants.DEFAULT_LIMIT_ASSET,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        sort: SortType? = SortType.ASC,
        pageToken: String? = null,
    ) = assetsRepository.getTrades(
        typeAsset = typeAsset,
        sort = sort,
        limit = limit,
        endDate = endDate,
        startDate = startDate,
        symbol = symbol,
        pageToken = pageToken,
    )
}