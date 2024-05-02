package dev.pinkroom.marketsight.domain.repository

import dev.pinkroom.marketsight.common.Constants
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.model.bars_asset.BarAsset
import dev.pinkroom.marketsight.domain.model.bars_asset.TimeFrame
import dev.pinkroom.marketsight.domain.model.quotes_asset.QuotesResponse
import dev.pinkroom.marketsight.domain.model.trades_asset.TradesResponse
import java.time.LocalDateTime

interface AssetsRepository {
    suspend fun getAllAssets(
        typeAsset: TypeAsset
    ): Resource<List<Asset>>

    suspend fun getBars(
        symbol: String,
        typeAsset: TypeAsset,
        timeFrame: TimeFrame? = TimeFrame.Day,
        limit: Int? = Constants.DEFAULT_LIMIT_ASSET,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        sort: SortType? = SortType.ASC,
    ): Resource<List<BarAsset>>

    suspend fun getTrades(
        symbol: String,
        typeAsset: TypeAsset,
        limit: Int? = Constants.DEFAULT_LIMIT_ASSET,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        sort: SortType? = SortType.ASC,
        pageToken: String? = null,
    ): Resource<TradesResponse>

    suspend fun getQuotes(
        symbol: String,
        typeAsset: TypeAsset,
        limit: Int? = Constants.DEFAULT_LIMIT_ASSET,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        sort: SortType? = SortType.ASC,
        pageToken: String? = null,
    ): Resource<QuotesResponse>
}