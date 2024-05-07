package dev.pinkroom.marketsight.data.repository

import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.data.data_source.AssetsRemoteDataSource
import dev.pinkroom.marketsight.data.data_source.MarketRemoteDataSource
import dev.pinkroom.marketsight.data.mapper.toAsset
import dev.pinkroom.marketsight.data.mapper.toBarAsset
import dev.pinkroom.marketsight.data.mapper.toQuoteAsset
import dev.pinkroom.marketsight.data.mapper.toQuotesResponse
import dev.pinkroom.marketsight.data.mapper.toSubscriptionMessage
import dev.pinkroom.marketsight.data.mapper.toTradeAsset
import dev.pinkroom.marketsight.data.mapper.toTradesResponse
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.model.bars_asset.BarAsset
import dev.pinkroom.marketsight.domain.model.bars_asset.TimeFrame
import dev.pinkroom.marketsight.domain.model.common.SubscriptionMessage
import dev.pinkroom.marketsight.domain.model.quotes_asset.QuotesResponse
import dev.pinkroom.marketsight.domain.model.trades_asset.TradesResponse
import dev.pinkroom.marketsight.domain.repository.AssetsRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single
import java.time.LocalDateTime
import javax.inject.Inject

class AssetsRepositoryImp @Inject constructor(
    private val assetsRemoteDataSource: AssetsRemoteDataSource,
    private val marketRemoteDataSource: MarketRemoteDataSource,
    private val dispatchers: DispatcherProvider,
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
            val response = marketRemoteDataSource.getBars(
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
            val response = marketRemoteDataSource.getTrades(
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
            val response = marketRemoteDataSource.getQuotes(
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

    override fun getRealTimeBars(symbol: String, typeAsset: TypeAsset) = flow {
        marketRemoteDataSource.getRealTimeBars(
            typeAsset = typeAsset
        ).collect{ response ->
            val dataRelatedToRequiredSymbol = response.filter { it.symbol == symbol }.map { it.toBarAsset() }
            dataRelatedToRequiredSymbol.takeIf { it.isNotEmpty() }?.let { emit(it) }
        }
    }.flowOn(dispatchers.IO)

    override fun getRealTimeTrades(symbol: String, typeAsset: TypeAsset) = flow {
        marketRemoteDataSource.getRealTimeTrades(
            typeAsset = typeAsset
        ).collect{ response ->
            val dataRelatedToRequiredSymbol = response.filter { it.symbol == symbol }.map { it.toTradeAsset() }
            dataRelatedToRequiredSymbol.takeIf { it.isNotEmpty() }?.let { emit(it) }
        }
    }.flowOn(dispatchers.IO)

    override fun getRealTimeQuotes(symbol: String, typeAsset: TypeAsset) = flow {
        marketRemoteDataSource.getRealTimeQuotes(
            typeAsset = typeAsset
        ).collect{ response ->
            val dataRelatedToRequiredSymbol = response.filter { it.symbol == symbol }.map { it.toQuoteAsset() }
            dataRelatedToRequiredSymbol.takeIf { it.isNotEmpty() }?.let { emit(it) }
        }
    }.flowOn(dispatchers.IO)

    override suspend fun subscribeUnsubscribeRealTimeFinancialData(
        action: ActionAlpaca,
        typeAsset: TypeAsset,
        symbol: String
    ): Resource<SubscriptionMessage> {
        return try {
            val response = marketRemoteDataSource.subscribeUnsubscribeRealTimeFinancialData(
                action = action, typeAsset = typeAsset, symbol = symbol,
            ).single()
            Resource.Success(data = response.toSubscriptionMessage())
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(message = e.message ?: "Something Went Wrong on Subscription Financial Asset")
        }
    }

    override fun statusService(typeAsset: TypeAsset) = marketRemoteDataSource.statusService(
        typeAsset = typeAsset,
    ).flowOn(dispatchers.IO)
}