package dev.pinkroom.marketsight.data.repository

import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.data.data_source.AssetsRemoteDataSource
import dev.pinkroom.marketsight.data.mapper.toAsset
import dev.pinkroom.marketsight.data.mapper.toBarAsset
import dev.pinkroom.marketsight.data.mapper.toQuotesResponse
import dev.pinkroom.marketsight.data.mapper.toTradesResponse
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.model.bars_asset.BarAsset
import dev.pinkroom.marketsight.domain.model.bars_asset.TimeFrame
import dev.pinkroom.marketsight.domain.model.quotes_asset.QuotesResponse
import dev.pinkroom.marketsight.domain.model.trades_asset.TradesResponse
import dev.pinkroom.marketsight.domain.repository.AssetsRepository
import java.time.LocalDateTime
import javax.inject.Inject

class AssetsRepositoryImp @Inject constructor(
    private val assetsRemoteDataSource: AssetsRemoteDataSource,
): AssetsRepository {
    override suspend fun getAllAssets(typeAsset: TypeAsset): Resource<List<Asset>> {
        return try {
            val response = assetsRemoteDataSource.getAllAssets(
                typeAsset = typeAsset,
            )
            Resource.Success(data = response.map { it.toAsset() })
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(message = e.message ?: "Something Went Wrong on Get all assets")
        }
    }

    override suspend fun getBars(
        symbol: String,
        typeAsset: TypeAsset,
        timeFrame: TimeFrame?,
        limit: Int?,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        sort: SortType?
    ): Resource<List<BarAsset>> {
        return try {
            val response = assetsRemoteDataSource.getBars(
                symbol = symbol,
                typeAsset = typeAsset,
                timeFrame = timeFrame,
                limit = limit,
                startDate = startDate,
                endDate = endDate,
            )
            Resource.Success(data = response.map { it.toBarAsset() })
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(message = e.message ?: "Something Went Wrong on Get Historical bars")
        }
    }

    override suspend fun getTrades(
        symbol: String,
        typeAsset: TypeAsset,
        limit: Int?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        sort: SortType?,
        pageToken: String?
    ): Resource<TradesResponse> {
        return try {
            val response = assetsRemoteDataSource.getTrades(
                symbol = symbol,
                typeAsset = typeAsset,
                limit = limit,
                startDate = startDate,
                endDate = endDate,
                sort = sort,
                pageToken = pageToken,
            )
            Resource.Success(data = response.toTradesResponse())
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(message = e.message ?: "Something Went Wrong on Get Historical trades")
        }
    }

    override suspend fun getQuotes(
        symbol: String,
        typeAsset: TypeAsset,
        limit: Int?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        sort: SortType?,
        pageToken: String?
    ): Resource<QuotesResponse> {
        return try {
            val response = assetsRemoteDataSource.getQuotes(
                symbol = symbol,
                typeAsset = typeAsset,
                limit = limit,
                startDate = startDate,
                endDate = endDate,
                sort = sort,
                pageToken = pageToken,
            )
            Resource.Success(data = response.toQuotesResponse())
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(message = e.message ?: "Something Went Wrong on Get Historical quotes")
        }
    }
}