package dev.pinkroom.marketsight.data.data_source

import com.google.gson.Gson
import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.Constants
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.HelperIdentifierMessagesAlpacaService
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.common.formatToStandardIso
import dev.pinkroom.marketsight.common.toObject
import dev.pinkroom.marketsight.common.verifyIfIsError
import dev.pinkroom.marketsight.data.mapper.toQuotesResponseDto
import dev.pinkroom.marketsight.data.mapper.toTradesResponseDto
import dev.pinkroom.marketsight.data.remote.AlpacaCryptoApi
import dev.pinkroom.marketsight.data.remote.AlpacaService
import dev.pinkroom.marketsight.data.remote.AlpacaStockApi
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.BarAssetDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.QuotesResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.TradesResponseDto
import dev.pinkroom.marketsight.data.remote.model.request.MessageAlpacaService
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.model.bars_asset.TimeFrame
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDateTime
import javax.inject.Inject

class MarketRemoteDataSource @Inject constructor(
    private val alpacaStockApi: AlpacaStockApi,
    private val alpacaServiceStock: AlpacaService,
    private val alpacaServiceCrypto: AlpacaService,
    private val alpacaCryptoApi: AlpacaCryptoApi,
    private val dispatchers: DispatcherProvider,
    private val gson: Gson,
) {
    suspend fun getBars(
        symbol: String,
        typeAsset: TypeAsset,
        timeFrame: TimeFrame? = TimeFrame.Day,
        limit: Int? = Constants.DEFAULT_LIMIT_ASSET,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        sort: SortType? = SortType.ASC,
    ): List<BarAssetDto> {
        return if (typeAsset is TypeAsset.Stock)
            alpacaStockApi.getHistoricalBarsStock(
                symbol = symbol,
                startDate = startDate.formatToStandardIso(),
                endDate = endDate.formatToStandardIso(),
                sort = sort?.type,
                limit = limit,
                timeFrame = timeFrame?.frameValue,
            ).bars ?: emptyList()
        else
            alpacaCryptoApi.getHistoricalBarsCrypto(
                symbol = symbol,
                startDate = startDate.formatToStandardIso(),
                endDate = endDate.formatToStandardIso(),
                sort = sort?.type,
                limit = limit,
                timeFrame = timeFrame?.frameValue,
            ).bars.entries.first().value
    }

    fun getRealTimeBars(
        typeAsset: TypeAsset,
    ) = getRealTimeData(
        typeAsset = typeAsset,
        helperIdentifierMessagesAlpacaService = HelperIdentifierMessagesAlpacaService.Bars
    ).flowOn(dispatchers.IO)

    suspend fun getTrades(
        symbol: String,
        typeAsset: TypeAsset,
        limit: Int? = Constants.DEFAULT_LIMIT_ASSET,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        sort: SortType? = SortType.ASC,
        pageToken: String? = null,
    ): TradesResponseDto {
        return if (typeAsset is TypeAsset.Stock)
            alpacaStockApi.getHistoricalTradesStock(
                symbol = symbol,
                startDate = startDate?.formatToStandardIso(),
                endDate = endDate?.formatToStandardIso(),
                sort = sort?.type,
                limit = limit,
                pageToken = pageToken
            )
        else
            alpacaCryptoApi.getHistoricalTradesCrypto(
                symbol = symbol,
                startDate = startDate?.formatToStandardIso(),
                endDate = endDate?.formatToStandardIso(),
                sort = sort?.type,
                limit = limit,
                pageToken = pageToken
            ).toTradesResponseDto()
    }

    fun getRealTimeTrades(
        typeAsset: TypeAsset,
    ) = getRealTimeData(
        typeAsset = typeAsset,
        helperIdentifierMessagesAlpacaService = HelperIdentifierMessagesAlpacaService.Trades
    ).flowOn(dispatchers.IO)

    suspend fun getQuotes(
        symbol: String,
        typeAsset: TypeAsset,
        limit: Int? = Constants.DEFAULT_LIMIT_ASSET,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        sort: SortType? = SortType.ASC,
        pageToken: String? = null,
    ): QuotesResponseDto {
        return if (typeAsset is TypeAsset.Stock)
            alpacaStockApi.getHistoricalQuotesStock(
                symbol = symbol,
                startDate = startDate?.formatToStandardIso(),
                endDate = endDate?.formatToStandardIso(),
                sort = sort?.type,
                limit = limit,
                pageToken = pageToken
            )
        else
            alpacaCryptoApi.getHistoricalQuotesCrypto(
                symbol = symbol,
                startDate = startDate?.formatToStandardIso(),
                endDate = endDate?.formatToStandardIso(),
                sort = sort?.type,
                limit = limit,
                pageToken = pageToken
            ).toQuotesResponseDto()
    }

    fun getRealTimeQuotes(
        typeAsset: TypeAsset,
    ) = getRealTimeData(
        typeAsset = typeAsset,
        helperIdentifierMessagesAlpacaService = HelperIdentifierMessagesAlpacaService.Quotes
    ).flowOn(dispatchers.IO)

    fun subscribeUnsubscribeRealTimeFinancialData(
        action: ActionAlpaca,
        typeAsset: TypeAsset,
        symbol: String,
    ) = flow {
        val serviceToUse = getServiceToUse(typeAsset = typeAsset)
        val symbols = listOf(symbol)
        val message = MessageAlpacaService(
            action = action.action, trades = symbols,
            quotes = symbols, bars = symbols,
        )
        serviceToUse.sendMessage(message = message)
        serviceToUse.observeResponse().collect { data ->
            data.forEach {
                gson.toObject(value = it, helperIdentifier = HelperIdentifierMessagesAlpacaService.Subscription)?.let { sub ->
                    emit(sub)
                    currentCoroutineContext().cancel()
                } ?: run {
                    if (gson.verifyIfIsError(it) != null){
                        throw Exception("Error on Subscription")
                    }
                }
            }
        }
    }.flowOn(dispatchers.IO)

    fun statusService(
        typeAsset: TypeAsset
    ) = flow {
        val serviceToUse = getServiceToUse(typeAsset = typeAsset)
        serviceToUse.observeOnConnectionEvent().collect(this)
    }.flowOn(dispatchers.IO)

    private fun <T> getRealTimeData(
        typeAsset: TypeAsset,
        helperIdentifierMessagesAlpacaService: HelperIdentifierMessagesAlpacaService<T>,
    ) = flow {
        val serviceToUse = getServiceToUse(typeAsset = typeAsset)
        serviceToUse.observeResponse().collect{ data ->
            val listObject = mutableListOf<T>()
            data.forEach {
                gson.toObject(value = it, helperIdentifier = helperIdentifierMessagesAlpacaService)?.let { response ->
                    listObject.add(response)
                }
            }
            if (listObject.isNotEmpty()){
                emit(listObject.toList())
            }
        }
    }.flowOn(dispatchers.IO)

    private fun getServiceToUse(typeAsset: TypeAsset) = if (typeAsset is TypeAsset.Stock) alpacaServiceStock
    else alpacaServiceCrypto
}