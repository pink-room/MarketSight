package dev.pinkroom.marketsight.domain.use_case.market

import dev.pinkroom.marketsight.common.Constants
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.repository.AssetsRepository
import java.time.LocalDateTime
import javax.inject.Inject

class GetQuotesAssetUseCase @Inject constructor(
    private val assetsRepository: AssetsRepository,
){
    suspend operator fun invoke(
        symbol: String,
        typeAsset: TypeAsset,
        limit: Int? = Constants.DEFAULT_LIMIT_QUOTES_ASSET,
        startDate: LocalDateTime = LocalDateTime.now().minusYears(7),
        endDate: LocalDateTime? = null,
        sort: SortType? = SortType.DESC,
        pageToken: String? = null,
    ) = assetsRepository.getQuotes(
        typeAsset = typeAsset,
        sort = sort,
        limit = limit,
        endDate = endDate,
        startDate = startDate,
        symbol = symbol,
        pageToken = pageToken,
    )
}