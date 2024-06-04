package dev.pinkroom.marketsight.domain.repository

import com.tinder.scarlet.WebSocket
import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.Constants
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.model.bars_asset.BarAsset
import dev.pinkroom.marketsight.domain.model.bars_asset.TimeFrame
import dev.pinkroom.marketsight.domain.model.common.SubscriptionMessage
import dev.pinkroom.marketsight.domain.model.quotes_asset.QuoteAsset
import dev.pinkroom.marketsight.domain.model.quotes_asset.QuotesResponse
import dev.pinkroom.marketsight.domain.model.trades_asset.TradeAsset
import dev.pinkroom.marketsight.domain.model.trades_asset.TradesResponse
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface AssetsRepository {
    suspend fun getAllAssets(
        typeAsset: TypeAsset,
        fetchFromRemote: Boolean,
    ): Resource<List<Asset>>

    suspend fun getAssetById(
        id: String
    ): Resource<Asset>

    suspend fun getBars(
        symbol: String,
        typeAsset: TypeAsset,
        timeFrame: TimeFrame? = TimeFrame.Day,
        limit: Int? = Constants.DEFAULT_LIMIT_ASSET,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        sort: SortType? = SortType.ASC,
    ): Resource<List<BarAsset>>

    suspend fun getLatestBar(
        symbol: String,
        typeAsset: TypeAsset,
    ): Resource<BarAsset>

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
        limit: Int? = Constants.DEFAULT_LIMIT_QUOTES_ASSET,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        sort: SortType? = SortType.ASC,
        pageToken: String? = null,
    ): Resource<QuotesResponse>

    fun getRealTimeBars(
        symbol: String,
        typeAsset: TypeAsset,
    ): Flow<List<BarAsset>>

    fun getRealTimeTrades(
        symbol: String,
        typeAsset: TypeAsset,
    ): Flow<List<TradeAsset>>

    fun getRealTimeQuotes(
        symbol: String,
        typeAsset: TypeAsset,
    ): Flow<List<QuoteAsset>>

    suspend fun subscribeUnsubscribeRealTimeFinancialData(
        action: ActionAlpaca,
        typeAsset: TypeAsset,
        symbol: String,
    ): Resource<SubscriptionMessage>

    fun statusService(
        typeAsset: TypeAsset
    ): Flow<WebSocket.Event>
}