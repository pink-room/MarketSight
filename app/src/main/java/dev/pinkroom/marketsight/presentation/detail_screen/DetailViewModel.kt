package dev.pinkroom.marketsight.presentation.detail_screen

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinder.scarlet.WebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.Constants.DEFAULT_LIMIT_QUOTES_ASSET
import dev.pinkroom.marketsight.common.Constants.DEFAULT_LIMIT_TRADES_ASSET
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.di.DetailScreenArgModule.SymbolId
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.assets.FilterAssetDetailInfo
import dev.pinkroom.marketsight.domain.model.bars_asset.AssetChartInfo
import dev.pinkroom.marketsight.domain.model.bars_asset.BarAsset
import dev.pinkroom.marketsight.domain.model.bars_asset.FilterHistoricalBar
import dev.pinkroom.marketsight.domain.use_case.assets.GetAssetById
import dev.pinkroom.marketsight.domain.use_case.market.GetBarsAsset
import dev.pinkroom.marketsight.domain.use_case.market.GetLatestBarAsset
import dev.pinkroom.marketsight.domain.use_case.market.GetQuotesAsset
import dev.pinkroom.marketsight.domain.use_case.market.GetRealTimeBarsAsset
import dev.pinkroom.marketsight.domain.use_case.market.GetRealTimeQuotesAsset
import dev.pinkroom.marketsight.domain.use_case.market.GetRealTimeTradesAsset
import dev.pinkroom.marketsight.domain.use_case.market.GetStatusServiceAsset
import dev.pinkroom.marketsight.domain.use_case.market.GetTradesAsset
import dev.pinkroom.marketsight.domain.use_case.market.SetSubscribeRealTimeAsset
import dev.pinkroom.marketsight.domain.use_case.market.SetUnsubscribeRealTimeAsset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class DetailViewModel @Inject constructor(
    @SymbolId private val symbolIdArg: String?,
    private val dispatchers: DispatcherProvider,
    private val getAssetById: GetAssetById,
    private val getBarsAsset: GetBarsAsset,
    private val getLatestBarAsset: GetLatestBarAsset,
    private val getStatusServiceAsset: GetStatusServiceAsset,
    private val setSubscribeRealTimeAsset: SetSubscribeRealTimeAsset,
    private val setUnsubscribeRealTimeAsset: SetUnsubscribeRealTimeAsset,
    private val getRealTimeBarsAsset: GetRealTimeBarsAsset,
    private val getQuotesAsset: GetQuotesAsset,
    private val getRealTimeQuotesAsset: GetRealTimeQuotesAsset,
    private val getTradesAsset: GetTradesAsset,
    private val getRealTimeTradesAsset: GetRealTimeTradesAsset,
): ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _action = Channel<DetailAction>()
    val action = _action.receiveAsFlow()

    private lateinit var symbolId: String
    private lateinit var asset: Asset
    private var errorOnGetAssetValue: Boolean = false
    private var latestValueAsset = 0.0
    private var isUserPressing = false

    private var jobGetHistoricalBars: Job? = null
    private var jobGetRealTimeBars: Job? = null
    private var jobGetRealTimeQuotes: Job? = null
    private var jobGetRealTimeTrades: Job? = null

    init {
        validateArgs()
    }

    fun onEvent(event: DetailEvent) {
        when(event) {
            is DetailEvent.ChangeFilterAssetChart -> changeFilterAssetChart(newFilter = event.newFilter)
            is DetailEvent.ChangeActualPriceToShow -> updateCurrentPriceToShow(
                value = event.priceToShow ?: latestValueAsset,
                valueToCompare = event.valueToCompare ?: uiState.value.assetCharInfo.barsInfo.first().closingPrice,
                isPressing = event.priceToShow != null
            )
            DetailEvent.RetryToGetAssetInfo -> retryToGetMainInfoAsset()
            DetailEvent.RetryToGetHistoricalBars -> retryToGetBarsInfo()
            DetailEvent.RetryToSubscribeRealTimeAsset -> subscribeRealTimeAsset()
            DetailEvent.RetryToGetQuotesAsset -> retryToGetQuotesInfo()
            DetailEvent.RetryToGetTradesAsset -> retryToGetTradesInfo()
            is DetailEvent.ChangeFilterAssetDetailInfo -> changeFilterAssetDetailInfo(newFilter = event.newFilter)
        }
    }

    private fun validateArgs() {
        viewModelScope.launch(dispatchers.Default) {
            if (symbolIdArg == null) _action.send(DetailAction.NavigateToHomeEmptyId)
            else {
                symbolId = symbolIdArg
                getDataAboutAsset()
            }
        }
    }

    private fun getDataAboutAsset() {
        viewModelScope.launch(dispatchers.IO) {
            when(val response = getAssetById(id = symbolId)) {
                is Resource.Success -> {
                    asset = response.data
                    _uiState.update { it.copy(asset = response.data) }

                    subscribeRealTimeAsset()
                    statusWSService()
                    getLatestValueAsset()
                    getHistoricalBarsInfo()
                    getQuotesAssetInfo()
                    getTradesAssetInfo()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(statusMainInfo = it.statusMainInfo.copy(isLoading = false, errorMessage = R.string.error_on_getting_assets)) }
                }
            }
        }
    }

    private fun getLatestValueAsset() {
        viewModelScope.launch(dispatchers.IO) {
            when(val response = getLatestBarAsset(symbol = asset.symbol, typeAsset = asset.getTypeAsset())) {
                is Resource.Success -> {
                    errorOnGetAssetValue = false
                    latestValueAsset = response.data.closingPrice
                    _uiState.update {
                        it.copy(
                            statusMainInfo = it.statusMainInfo.copy(isLoading = false, errorMessage = null),
                            currentPriceInfo = it.currentPriceInfo.copy(price = latestValueAsset),
                        )
                    }
                }
                is Resource.Error -> {
                    errorOnGetAssetValue = true
                    _uiState.update { it.copy(statusMainInfo = it.statusMainInfo.copy(isLoading = false, errorMessage = R.string.error_on_getting_assets)) }
                }
            }
        }
    }

    private fun getHistoricalBarsInfo() {
        jobGetHistoricalBars = viewModelScope.launch(dispatchers.IO) {
            _uiState.update { it.copy(statusHistoricalBars = it.statusHistoricalBars.copy(isLoading = true, errorMessage = null)) }
            val selectedFilter = uiState.value.selectedFilterHistorical
            val response = getBarsAsset(
                symbol = asset.symbol,
                typeAsset = asset.getTypeAsset(),
                timeFrame = selectedFilter.timeFrameIntervalValues,
                startDate = selectedFilter.getStarLocalDateTime(),
                endDate = selectedFilter.getEndLocalDateTime(),
            )
            when(response) {
                is Resource.Success -> {
                    updateAssetChartInfo(data = response.data, isToUpdateStatusBars = true)
                    jobGetRealTimeBars?.let { if (it.isActive) it.cancel() }
                    observeRealTimeBars()
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            statusHistoricalBars = it.statusHistoricalBars.copy(isLoading = false, errorMessage = R.string.error_on_getting_bars),
                            assetCharInfo = AssetChartInfo(),
                        )
                    }
                }
            }
        }
    }

    private fun statusWSService() {
        viewModelScope.launch(dispatchers.IO) {
            getStatusServiceAsset(typeAsset = asset.getTypeAsset())
                .filter { it is WebSocket.Event.OnConnectionOpened<*> }
                .collect{
                    subscribeRealTimeAsset()
                }
        }
    }

    private fun subscribeRealTimeAsset() {
        viewModelScope.launch(dispatchers.IO) {
            val response = setSubscribeRealTimeAsset(symbol = asset.symbol, typeAsset = asset.getTypeAsset())
            when(response) {
                is Resource.Success -> {
                    val data = response.data
                    val symbolsSubscribed = listOf(data.bars, data.quotes, data.trades).flatMap { it.orEmpty() }.distinct()
                    symbolsSubscribed.forEach { symbol ->
                        if (symbol != asset.symbol) setUnsubscribeRealTimeAsset(symbol = symbol, typeAsset = asset.getTypeAsset())
                    }
                }
                is Resource.Error -> _action.send(
                    DetailAction.ShowSnackBar(
                        message = R.string.error_subscription_real_time_asset,
                        duration = SnackbarDuration.Indefinite,
                        actionMessage = R.string.retry,
                    )
                )
            }
        }
    }

    private fun observeRealTimeBars() {
        jobGetRealTimeBars = viewModelScope.launch(dispatchers.IO) {
            getRealTimeBarsAsset(symbol = asset.symbol, typeAsset = asset.getTypeAsset()).collect { response ->
                updateAssetChartInfo(data = uiState.value.assetCharInfo.barsInfo + response)
                latestValueAsset = response.last().closingPrice
                if (!isUserPressing) updateCurrentPriceToShow(
                    value = latestValueAsset,
                    valueToCompare = uiState.value.assetCharInfo.barsInfo.first().closingPrice,
                )
            }
        }
    }

    private fun updateAssetChartInfo(data: List<BarAsset>, isToUpdateStatusBars: Boolean = false) {
        val maxValue = data.maxOfOrNull { it.closingPrice } ?: 0.0
        val minValue = data.minOfOrNull { it.closingPrice } ?: 0.0

        if (isToUpdateStatusBars) {
            _uiState.update {
                it.copy(
                    statusHistoricalBars = it.statusHistoricalBars.copy(
                        isLoading = false,
                        errorMessage = null
                    ),
                    assetCharInfo = it.assetCharInfo.copy(
                        upperValue = maxValue,
                        lowerValue = minValue,
                        barsInfo = data
                    ),
                )
            }
            if (!isUserPressing && data.isNotEmpty())
                updateCurrentPriceToShow(value = data.last().closingPrice, valueToCompare = data.first().closingPrice)
        } else _uiState.update {
            it.copy(
                assetCharInfo = it.assetCharInfo.copy(upperValue = maxValue, lowerValue = minValue, barsInfo = data),
            )
        }
    }

    private fun getQuotesAssetInfo() {
        viewModelScope.launch(dispatchers.IO) {
            val response = getQuotesAsset(symbol = asset.symbol, typeAsset = asset.getTypeAsset())
            when(response) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            statusQuotes = it.statusQuotes.copy(isLoading = false),
                            latestQuotes = response.data.quotes,
                        )
                    }
                    jobGetRealTimeQuotes?.let { if (it.isActive) it.cancel() }
                    observeRealTimeQuotes()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(statusQuotes = it.statusQuotes.copy(isLoading = false, errorMessage = R.string.error_on_getting_quotes)) }
                }
            }
        }
    }

    private fun observeRealTimeQuotes() {
        jobGetRealTimeQuotes = viewModelScope.launch(dispatchers.IO) {
            getRealTimeQuotesAsset(symbol = asset.symbol, typeAsset = asset.getTypeAsset()).collect { response ->
                if (!uiState.value.statusQuotes.isLoading) {
                    val newQuotesList = (response + uiState.value.latestQuotes).take(DEFAULT_LIMIT_QUOTES_ASSET)
                    _uiState.update { it.copy(latestQuotes = newQuotesList) }
                }
            }
        }
    }

    private fun getTradesAssetInfo() {
        viewModelScope.launch(dispatchers.IO) {
            val response = getTradesAsset(symbol = asset.symbol, typeAsset = asset.getTypeAsset())
            when(response) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            statusTrades = it.statusTrades.copy(isLoading = false),
                            latestTrades = response.data.trades,
                        )
                    }
                    jobGetRealTimeTrades?.let { if (it.isActive) it.cancel() }
                    observeRealTimeTrades()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(statusTrades = it.statusTrades.copy(isLoading = false, errorMessage = R.string.error_on_getting_trades)) }
                }
            }
        }
    }

    private fun observeRealTimeTrades() {
        jobGetRealTimeTrades = viewModelScope.launch(dispatchers.IO) {
            getRealTimeTradesAsset(symbol = asset.symbol, typeAsset = asset.getTypeAsset()).collect { response ->
                if (!uiState.value.statusTrades.isLoading) {
                    val newTradesList = (response + uiState.value.latestTrades).take(DEFAULT_LIMIT_TRADES_ASSET)
                    _uiState.update { it.copy(latestTrades = newTradesList) }
                }
            }
        }
    }

    private fun changeFilterAssetChart(newFilter: FilterHistoricalBar) {
        if (newFilter == uiState.value.selectedFilterHistorical) return
        _uiState.update { it.copy(selectedFilterHistorical = newFilter) }
        jobGetHistoricalBars?.let { if (it.isActive) it.cancel() }
        getHistoricalBarsInfo()
    }

    private fun updateCurrentPriceToShow(value: Double, valueToCompare: Double, isPressing: Boolean = false) {
        isUserPressing = isPressing
        val percentage = calculatePercentageCurrentPrice(valueToCompare = valueToCompare, value = value)
        val priceDifference = value - valueToCompare
        _uiState.update { it.copy(currentPriceInfo = it.currentPriceInfo.copy(price = value, percentage = percentage, priceDifference = priceDifference)) }
    }

    private fun calculatePercentageCurrentPrice(valueToCompare: Double, value: Double) = abs(((value - valueToCompare) / valueToCompare) * 100)

    private fun retryToGetMainInfoAsset() {
        _uiState.update { it.copy(statusMainInfo = it.statusMainInfo.copy(isLoading = true, errorMessage = null)) }
        if (errorOnGetAssetValue) getLatestValueAsset()
        else getDataAboutAsset()
    }

    private fun retryToGetBarsInfo() {
        _uiState.update { it.copy(statusHistoricalBars = it.statusHistoricalBars.copy(isLoading = true, errorMessage = null)) }
        getHistoricalBarsInfo()
    }

    private fun retryToGetQuotesInfo() {
        _uiState.update { it.copy(statusQuotes = it.statusQuotes.copy(isLoading = true, errorMessage = null)) }
        getQuotesAssetInfo()
    }

    private fun retryToGetTradesInfo() {
        _uiState.update { it.copy(statusTrades = it.statusTrades.copy(isLoading = true, errorMessage = null)) }
        getTradesAssetInfo()
    }

    private fun changeFilterAssetDetailInfo(newFilter: FilterAssetDetailInfo) {
        _uiState.update { it.copy(selectedFilterDetailInfo = newFilter) }
    }

    override fun onCleared() {
        super.onCleared()
        CoroutineScope(dispatchers.IO).launch {
            val response = setUnsubscribeRealTimeAsset(symbol = asset.symbol, typeAsset = asset.getTypeAsset())
            when(response) {
                is Resource.Error -> Log.d("TESTE","ERRO")
                is Resource.Success -> Log.d("TESTE","OK UNS")
            }
        }
    }
}