package dev.pinkroom.marketsight.domain.use_case.assets

import dev.pinkroom.marketsight.common.Constants
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.model.bars_asset.TimeFrame
import dev.pinkroom.marketsight.domain.repository.AssetsRepository
import java.time.LocalDateTime
import javax.inject.Inject

class GetBarsAssets @Inject constructor(
    private val assetsRepository: AssetsRepository,
){
    suspend operator fun invoke(
        symbol: String,
        typeAsset: TypeAsset,
        timeFrame: TimeFrame? = TimeFrame.Day,
        limit: Int? = Constants.DEFAULT_LIMIT_ASSET,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        sort: SortType? = SortType.ASC,
    ) = assetsRepository.getBars(
        typeAsset = typeAsset,
        sort = sort,
        timeFrame = timeFrame,
        limit = limit,
        endDate = endDate,
        startDate = startDate,
        symbol = symbol,
    )
}