package dev.pinkroom.marketsight.presentation.detail_screen

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.tinder.scarlet.WebSocket
import dev.pinkroom.marketsight.common.Constants
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.data.mapper.toBarAsset
import dev.pinkroom.marketsight.data.mapper.toQuoteAsset
import dev.pinkroom.marketsight.data.mapper.toTradeAsset
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.bars_asset.BarAsset
import dev.pinkroom.marketsight.domain.model.common.SubscriptionMessage
import dev.pinkroom.marketsight.domain.model.quotes_asset.QuoteAsset
import dev.pinkroom.marketsight.domain.model.quotes_asset.QuotesResponse
import dev.pinkroom.marketsight.domain.model.trades_asset.TradeAsset
import dev.pinkroom.marketsight.domain.model.trades_asset.TradesResponse
import dev.pinkroom.marketsight.domain.use_case.assets.GetAssetByIdUseCase
import dev.pinkroom.marketsight.domain.use_case.market.GetBarsAssetUseCase
import dev.pinkroom.marketsight.domain.use_case.market.GetLatestBarAssetUseCase
import dev.pinkroom.marketsight.domain.use_case.market.GetQuotesAssetUseCase
import dev.pinkroom.marketsight.domain.use_case.market.GetRealTimeBarsAssetUseCase
import dev.pinkroom.marketsight.domain.use_case.market.GetRealTimeQuotesAssetUseCase
import dev.pinkroom.marketsight.domain.use_case.market.GetRealTimeTradesAssetUseCase
import dev.pinkroom.marketsight.domain.use_case.market.GetStatusServiceAssetUseCase
import dev.pinkroom.marketsight.domain.use_case.market.GetTradesAssetUseCase
import dev.pinkroom.marketsight.domain.use_case.market.SetSubscribeRealTimeAssetUseCase
import dev.pinkroom.marketsight.domain.use_case.market.SetUnsubscribeRealTimeAssetUseCase
import dev.pinkroom.marketsight.factories.BarAssetDtoFactory
import dev.pinkroom.marketsight.factories.QuoteAssetDtoFactory
import dev.pinkroom.marketsight.factories.TradeAssetDtoFactory
import dev.pinkroom.marketsight.util.MainCoroutineRule
import dev.pinkroom.marketsight.util.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.math.abs

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val barAssetFactory = BarAssetDtoFactory()
    private val quotesFactory = QuoteAssetDtoFactory()
    private val tradeFactory = TradeAssetDtoFactory()
    private val dispatchers = TestDispatcherProvider()
    private val getAssetByIdUseCase = mockk<GetAssetByIdUseCase>(relaxed = true, relaxUnitFun = true)
    private val getBarsAssetUseCase = mockk<GetBarsAssetUseCase>(relaxed = true, relaxUnitFun = true)
    private val getLatestBarAssetUseCase = mockk<GetLatestBarAssetUseCase>(relaxed = true, relaxUnitFun = true)
    private val getQuoteAsset = mockk<GetQuotesAssetUseCase>(relaxed = true, relaxUnitFun = true)
    private val getRealTimeBarAsset = mockk<GetRealTimeBarsAssetUseCase>(relaxed = true, relaxUnitFun = true)
    private val getRealTimeQuotesAssetUseCase = mockk<GetRealTimeQuotesAssetUseCase>(relaxed = true, relaxUnitFun = true)
    private val getRealTimeTradesAssetUseCase = mockk<GetRealTimeTradesAssetUseCase>(relaxed = true, relaxUnitFun = true)
    private val getStatusServiceAssetUseCase = mockk<GetStatusServiceAssetUseCase>(relaxed = true, relaxUnitFun = true)
    private val getTradesAssetUseCase = mockk<GetTradesAssetUseCase>(relaxed = true, relaxUnitFun = true)
    private val setSubscribeRealTimeAssetUseCase = mockk<SetSubscribeRealTimeAssetUseCase>(relaxed = true, relaxUnitFun = true)
    private val setUnsubscribeRealTimeAssetUseCase = mockk<SetUnsubscribeRealTimeAssetUseCase>(relaxed = true, relaxUnitFun = true)
    private var symbolIdArg: String? = "TSLA"
    private val asset = Asset(name = "Tesla", symbol = symbolIdArg ?: "", isStock = true)
    private lateinit var detailViewModel: DetailViewModel

    private fun initViewModel(assetArg: Asset? = asset) {
        if (assetArg != null) mockResponseGetAssetById(asset = asset)

        detailViewModel = DetailViewModel(
            getAssetByIdUseCase = getAssetByIdUseCase,
            getBarsAssetUseCase = getBarsAssetUseCase,
            getLatestBarAssetUseCase = getLatestBarAssetUseCase,
            getQuotesAssetUseCase = getQuoteAsset,
            getRealTimeBarsAssetUseCase = getRealTimeBarAsset,
            getRealTimeQuotesAssetUseCase = getRealTimeQuotesAssetUseCase,
            getRealTimeTradesAssetUseCase = getRealTimeTradesAssetUseCase,
            getStatusServiceAssetUseCase = getStatusServiceAssetUseCase,
            getTradesAssetUseCase = getTradesAssetUseCase,
            setSubscribeRealTimeAssetUseCase = setSubscribeRealTimeAssetUseCase,
            setUnsubscribeRealTimeAssetUseCase = setUnsubscribeRealTimeAssetUseCase,
            symbolIdArg = symbolIdArg,
            dispatchers = dispatchers,
        )
    }

    @Test
    fun `When init VM, Then Error in validate args`() = runTest {
        // GIVEN
        symbolIdArg = null

        // WHEN
        initViewModel()
        val action = detailViewModel.action.first()

        // THEN
        assertThat(action).isEqualTo(DetailAction.NavigateToHomeEmptyId)
    }

    @Test
    fun `When init VM, Then Error in getDataAboutAsset`() = runTest {
        // GIVEN
        mockResponseGetAssetById(asset = null)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = detailViewModel.uiState.value
        assertThat(uiState.statusMainInfo.isLoading).isEqualTo(false)
        assertThat(uiState.statusMainInfo.errorMessage).isNotNull()
        coVerify { getAssetByIdUseCase.invoke(id = any()) }
    }

    @Test
    fun `When init VM, Then Success in getDataAboutAsset`() = runTest {
        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = detailViewModel.uiState.value
        assertThat(uiState.statusMainInfo.isLoading).isEqualTo(false)
        assertThat(uiState.statusMainInfo.errorMessage).isNotNull()
        assertThat(uiState.asset).isEqualTo(asset)
    }

    @Test
    fun `When init VM, Then Success in subscribeRealTimeAsset`() = runTest {
        // GIVEN
        val message = SubscriptionMessage(quotes = listOf(asset.symbol), bars = listOf(asset.symbol), trades = listOf(asset.symbol))
        mockSubscribeRealTimeAsset(message = message)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        coVerify(exactly = 1) { setSubscribeRealTimeAssetUseCase(symbol = asset.symbol, typeAsset = asset.getTypeAsset()) }
    }

    @Test
    fun `When init VM, Then Success in subscribeRealTimeAsset but more than one asset is subscribed`() = runTest {
        // GIVEN
        val otherAssetSubscribed = "AAPL"
        val message = SubscriptionMessage(quotes = listOf(asset.symbol, otherAssetSubscribed), bars = listOf(asset.symbol, otherAssetSubscribed), trades = listOf(asset.symbol))
        val messageAfterUnsubscribe = SubscriptionMessage(quotes = listOf(asset.symbol), bars = listOf(asset.symbol), trades = listOf(asset.symbol))
        mockSubscribeRealTimeAsset(message = message)
        mockUnSubscribeRealTimeAsset(message = messageAfterUnsubscribe)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        coVerify(exactly = 1) { setSubscribeRealTimeAssetUseCase(symbol = asset.symbol, typeAsset = asset.getTypeAsset()) }
        coVerify(exactly = 1) { setUnsubscribeRealTimeAssetUseCase(symbol = otherAssetSubscribed, typeAsset = asset.getTypeAsset()) }
    }

    @Test
    fun `When init VM, Then Error in subscribeRealTimeAsset`() = runTest {
        // GIVEN
        mockSubscribeRealTimeAsset(message = null)

        // WHEN
        initViewModel()
        val action = detailViewModel.action.first()

        // THEN
        assertThat(action).isNotNull()
        assertThat(action).isInstanceOf(DetailAction.ShowSnackBar::class.java)
        coVerify(exactly = 1) { setSubscribeRealTimeAssetUseCase(symbol = asset.symbol, typeAsset = asset.getTypeAsset()) }
    }

    @Test
    fun `When Retry to subscribeRealTimeAsset, Then Success in subscribeRealTimeAsset`() = runTest {
        // GIVEN
        val message = SubscriptionMessage(quotes = listOf(asset.symbol), bars = listOf(asset.symbol), trades = listOf(asset.symbol))
        mockRetrySubscribeRealTimeAsset(message = message)
        initViewModel()
        val action = detailViewModel.action.first()
        advanceUntilIdle()

        // WHEN
        assertThat(action).isNotNull()
        assertThat(action).isInstanceOf(DetailAction.ShowSnackBar::class.java)
        detailViewModel.onEvent(event = DetailEvent.RetryToSubscribeRealTimeAsset)
        advanceUntilIdle()

        // THEN
        coVerify(exactly = 2) { setSubscribeRealTimeAssetUseCase(symbol = asset.symbol, typeAsset = asset.getTypeAsset()) }
    }

    @Test
    fun `When statusWSService receive OnConnectionOpened, Then Subscribe Asset`() = runTest {
        // GIVEN
        val message = SubscriptionMessage(quotes = listOf(asset.symbol), bars = listOf(asset.symbol), trades = listOf(asset.symbol))
        mockSubscribeRealTimeAsset(message = message)
        mockStatusServiceAsset()

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        coVerify(exactly = 2) { setSubscribeRealTimeAssetUseCase(symbol = asset.symbol, typeAsset = asset.getTypeAsset()) }
    }

    @Test
    fun `When init VM, Then Success in getLatestValueAsset`() = runTest {
        // GIVEN
        val barAsset = BarAsset(closingPrice = 180.0)
        mockGetLatestBarAsset(barAsset = barAsset)
        mockSubscribeRealTimeAsset(message = null)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = detailViewModel.uiState.value
        assertThat(uiState.currentPriceInfo.price).isEqualTo(barAsset.closingPrice)
        assertThat(uiState.statusMainInfo.isLoading).isFalse()
        assertThat(uiState.statusMainInfo.errorMessage).isNull()
        coVerify(exactly = 1) { getLatestBarAssetUseCase(symbol = asset.symbol, typeAsset = asset.getTypeAsset()) }
    }

    @Test
    fun `When init VM, Then Error in getLatestValueAsset`() = runTest {
        // GIVEN
        mockGetLatestBarAsset(barAsset = null)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = detailViewModel.uiState.value
        assertThat(uiState.statusMainInfo.isLoading).isFalse()
        assertThat(uiState.statusMainInfo.errorMessage).isNotNull()
        coVerify(exactly = 1) { getLatestBarAssetUseCase(symbol = asset.symbol, typeAsset = asset.getTypeAsset()) }
    }

    @Test
    fun `When Retry to getAssetInfo when error occurred in getLatestBarAsset, Then just call getLatestBarAsset that will return success result`() = runTest {
        // GIVEN
        val barAsset = BarAsset(closingPrice = 180.0)
        mockGetLatestBarAssetRetry(barAsset = barAsset)
        initViewModel()
        advanceUntilIdle()

        // WHEN
        var uiState = detailViewModel.uiState.value
        assertThat(uiState.statusMainInfo.isLoading).isFalse()
        assertThat(uiState.statusMainInfo.errorMessage).isNotNull()
        detailViewModel.onEvent(event = DetailEvent.RetryToGetAssetInfo)
        advanceUntilIdle()

        // THEN
        uiState = detailViewModel.uiState.value
        assertThat(uiState.statusMainInfo.isLoading).isFalse()
        assertThat(uiState.statusMainInfo.errorMessage).isNull()
        assertThat(uiState.currentPriceInfo.price).isEqualTo(barAsset.closingPrice)
        coVerify(exactly = 1) { getAssetByIdUseCase(id = any()) }
        coVerify(exactly = 2) { getLatestBarAssetUseCase(symbol = asset.symbol, typeAsset = asset.getTypeAsset()) }
    }

    @Test
    fun `When Retry to getAssetInfo when error occurred in getAssetById, Then Success in getAssetInfo`() = runTest {
        // GIVEN
        val barAsset = BarAsset(closingPrice = 180.0)
        mockGetLatestBarAsset(barAsset = barAsset)
        mockResponseGetAssetByIdRetry(asset = asset)
        initViewModel(assetArg = null)
        advanceUntilIdle()

        // WHEN
        var uiState = detailViewModel.uiState.value
        assertThat(uiState.statusMainInfo.isLoading).isFalse()
        assertThat(uiState.statusMainInfo.errorMessage).isNotNull()
        detailViewModel.onEvent(event = DetailEvent.RetryToGetAssetInfo)
        advanceUntilIdle()

        // THEN
        uiState = detailViewModel.uiState.value
        assertThat(uiState.statusMainInfo.isLoading).isFalse()
        assertThat(uiState.statusMainInfo.errorMessage).isNull()
        assertThat(uiState.asset).isEqualTo(asset)
        coVerify(exactly = 2) { getAssetByIdUseCase(id = any()) }
        coVerify(exactly = 1) { getLatestBarAssetUseCase(symbol = asset.symbol, typeAsset = asset.getTypeAsset()) }
    }

    @Test
    fun `When init VM, Then Success in getHistoricalBarsInfo`() = runTest {
        // GIVEN
        val barAssets = barAssetFactory.buildList(number = 30).map { it.toBarAsset() }
        mockGetBarsAsset(barAsset = barAssets)
        val lastBar = barAssets.last()
        val firstBar = barAssets.first()
        val expectedPercentage = abs(((lastBar.closingPrice - firstBar.closingPrice) / firstBar.closingPrice) * 100)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = detailViewModel.uiState.value
        assertThat(uiState.statusHistoricalBars.isLoading).isFalse()
        assertThat(uiState.statusHistoricalBars.errorMessage).isNull()
        assertThat(uiState.assetCharInfo.barsInfo).isEqualTo(barAssets)
        assertThat(uiState.assetCharInfo.upperValue).isEqualTo(barAssets.maxOf { it.closingPrice })
        assertThat(uiState.assetCharInfo.lowerValue).isEqualTo(barAssets.minOf { it.closingPrice })
        assertThat(uiState.currentPriceInfo.price).isEqualTo(lastBar.closingPrice)
        assertThat(uiState.currentPriceInfo.priceDifference).isEqualTo(lastBar.closingPrice - firstBar.closingPrice)
        assertThat(uiState.currentPriceInfo.percentage).isEqualTo(expectedPercentage)
        coVerify(exactly = 1) {
            getBarsAssetUseCase(
                symbol = asset.symbol, typeAsset = asset.getTypeAsset(), startDate = any(),
                endDate = any(), timeFrame = any(), limit = any(), sort = any()
            )
        }
    }

    @Test
    fun `When init VM, Then Error in getHistoricalBarsInfo`() = runTest {
        // GIVEN
        mockGetBarsAsset(barAsset = null)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = detailViewModel.uiState.value
        assertThat(uiState.statusHistoricalBars.isLoading).isFalse()
        assertThat(uiState.statusHistoricalBars.errorMessage).isNotNull()
        coVerify(exactly = 1) {
            getBarsAssetUseCase(
                symbol = asset.symbol, typeAsset = asset.getTypeAsset(), startDate = any(),
                endDate = any(), timeFrame = any(), limit = any(), sort = any()
            )
        }
    }

    @Test
    fun `When Retry to getHistoricalBarsInfo, Then Success in getHistoricalBarsInfo`() = runTest {
        // GIVEN
        val barAssets = barAssetFactory.buildList(number = 30).map { it.toBarAsset() }
        mockRetryGetBarsAsset(barAsset = barAssets)
        initViewModel()
        advanceUntilIdle()

        // WHEN
        var uiState = detailViewModel.uiState.value
        assertThat(uiState.statusHistoricalBars.isLoading).isFalse()
        assertThat(uiState.statusHistoricalBars.errorMessage).isNotNull()
        detailViewModel.onEvent(event = DetailEvent.RetryToGetHistoricalBars)
        advanceUntilIdle()

        // THEN
        uiState = detailViewModel.uiState.value
        assertThat(uiState.statusHistoricalBars.isLoading).isFalse()
        assertThat(uiState.statusHistoricalBars.errorMessage).isNull()
        assertThat(uiState.assetCharInfo.barsInfo).isEqualTo(barAssets)
        coVerify(exactly = 2) {
            getBarsAssetUseCase(
                symbol = asset.symbol, typeAsset = asset.getTypeAsset(), startDate = any(),
                endDate = any(), timeFrame = any(), limit = any(), sort = any()
            )
        }
    }

    @Test
    fun `When init VM, Then collect data from observeRealTimeBars`() = runTest {
        // GIVEN
        val barAssets = barAssetFactory.buildList(number = 30).map { it.toBarAsset() }
        val barAssetsRealTime = barAssetFactory.buildList(number = 2).map { it.toBarAsset() }
        mockGetBarsAsset(barAsset = barAssets)
        mockGetRealTimeBarsAsset(barAsset = barAssetsRealTime)

        val expectedNewBarData = barAssets + barAssetsRealTime
        val lastBar = expectedNewBarData.last()
        val firstBar = expectedNewBarData.first()
        val expectedPercentage = abs(((lastBar.closingPrice - firstBar.closingPrice) / firstBar.closingPrice) * 100)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = detailViewModel.uiState.value
        assertThat(uiState.assetCharInfo.barsInfo).isEqualTo(expectedNewBarData)
        assertThat(uiState.assetCharInfo.lowerValue).isEqualTo(expectedNewBarData.minOf { it.closingPrice })
        assertThat(uiState.assetCharInfo.upperValue).isEqualTo(expectedNewBarData.maxOf { it.closingPrice })
        assertThat(uiState.currentPriceInfo.price).isEqualTo(lastBar.closingPrice)
        assertThat(uiState.currentPriceInfo.priceDifference).isEqualTo(lastBar.closingPrice - firstBar.closingPrice)
        assertThat(uiState.currentPriceInfo.percentage).isEqualTo(expectedPercentage)
        coVerify(exactly = 1) { getRealTimeBarAsset(symbol = asset.symbol, typeAsset = asset.getTypeAsset()) }
    }

    @Test
    fun `When init VM, Then Success in getQuotesAssetInfo`() = runTest {
        // GIVEN
        val quotes = quotesFactory.buildList(number = 30).map { it.toQuoteAsset() }
        mockGetQuotesAsset(quotesAsset = quotes)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = detailViewModel.uiState.value
        assertThat(uiState.statusQuotes.isLoading).isFalse()
        assertThat(uiState.statusQuotes.errorMessage).isNull()
        assertThat(uiState.latestQuotes).isEqualTo(quotes)
        coVerify(exactly = 1) { getQuoteAsset(symbol = asset.symbol, typeAsset = asset.getTypeAsset(), startDate = any()) }
    }

    @Test
    fun `When init VM, Then Error in getQuotesAssetInfo`() = runTest {
        // GIVEN
        mockGetQuotesAsset(quotesAsset = null)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = detailViewModel.uiState.value
        assertThat(uiState.statusQuotes.isLoading).isFalse()
        assertThat(uiState.statusQuotes.errorMessage).isNotNull()
        coVerify(exactly = 1) { getQuoteAsset(symbol = asset.symbol, typeAsset = asset.getTypeAsset(), startDate = any()) }
    }

    @Test
    fun `When Retry to getQuotesAssetInfo, Then Success in getQuotesAssetInfo`() = runTest {
        // GIVEN
        val quotes = quotesFactory.buildList(number = 30).map { it.toQuoteAsset() }
        mockRetryGetQuotesAsset(quotesAsset = quotes)
        initViewModel()
        advanceUntilIdle()

        // WHEN
        var uiState = detailViewModel.uiState.value
        assertThat(uiState.statusQuotes.isLoading).isFalse()
        assertThat(uiState.statusQuotes.errorMessage).isNotNull()
        detailViewModel.onEvent(event = DetailEvent.RetryToGetQuotesAsset)
        advanceUntilIdle()

        // THEN
        uiState = detailViewModel.uiState.value
        assertThat(uiState.statusQuotes.isLoading).isFalse()
        assertThat(uiState.statusQuotes.errorMessage).isNull()
        assertThat(uiState.latestQuotes).isEqualTo(quotes)
        coVerify(exactly = 2) { getQuoteAsset(symbol = asset.symbol, typeAsset = asset.getTypeAsset(), startDate = any()) }
    }

    @Test
    fun `When init VM, Then collect data from observeRealTimeQuotes`() = runTest {
        // GIVEN
        val quotes = quotesFactory.buildList(number = 30).map { it.toQuoteAsset() }
        val quotesRealTime = quotesFactory.buildList(number = 2).map { it.toQuoteAsset() }
        mockGetQuotesAsset(quotesAsset = quotes)
        mockGetRealTimeQuoteAsset(quotesAsset = quotesRealTime)
        val expectedQuotesData = (quotesRealTime + quotes).take(Constants.DEFAULT_LIMIT_QUOTES_ASSET)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = detailViewModel.uiState.value
        assertThat(uiState.latestQuotes).isEqualTo(expectedQuotesData)
        coVerify(exactly = 1) { getRealTimeQuotesAssetUseCase(symbol = asset.symbol, typeAsset = asset.getTypeAsset()) }
    }

    @Test
    fun `When init VM, Then Success in getTradesAssetInfo`() = runTest {
        // GIVEN
        val trades = tradeFactory.buildList(number = 30).map { it.toTradeAsset() }
        mockGetTradesAsset(tradesAsset = trades)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = detailViewModel.uiState.value
        assertThat(uiState.statusTrades.isLoading).isFalse()
        assertThat(uiState.statusTrades.errorMessage).isNull()
        assertThat(uiState.latestTrades).isEqualTo(trades)
        coVerify(exactly = 1) { getTradesAssetUseCase(symbol = asset.symbol, typeAsset = asset.getTypeAsset(), startDate = any()) }
    }

    @Test
    fun `When init VM, Then Error in getTradesAssetInfo`() = runTest {
        // GIVEN
        mockGetTradesAsset(tradesAsset = null)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = detailViewModel.uiState.value
        assertThat(uiState.statusTrades.isLoading).isFalse()
        assertThat(uiState.statusTrades.errorMessage).isNotNull()
        coVerify(exactly = 1) { getQuoteAsset(symbol = asset.symbol, typeAsset = asset.getTypeAsset(), startDate = any()) }
    }

    @Test
    fun `When Retry to getTradesAssetInfo, Then Success in getTradesAssetInfo`() = runTest {
        // GIVEN
        val trades = tradeFactory.buildList(number = 30).map { it.toTradeAsset() }
        mockRetryGetTradesAsset(tradesAsset = trades)
        initViewModel()
        advanceUntilIdle()

        // WHEN
        var uiState = detailViewModel.uiState.value
        assertThat(uiState.statusTrades.isLoading).isFalse()
        assertThat(uiState.statusTrades.errorMessage).isNotNull()
        detailViewModel.onEvent(event = DetailEvent.RetryToGetTradesAsset)
        advanceUntilIdle()

        // THEN
        uiState = detailViewModel.uiState.value
        assertThat(uiState.statusTrades.isLoading).isFalse()
        assertThat(uiState.statusTrades.errorMessage).isNull()
        assertThat(uiState.latestTrades).isEqualTo(trades)
        coVerify(exactly = 2) { getTradesAssetUseCase(symbol = asset.symbol, typeAsset = asset.getTypeAsset(), startDate = any()) }
    }

    @Test
    fun `When init VM, Then collect data from observeRealTimeTrades`() = runTest {
        // GIVEN
        val trades = tradeFactory.buildList(number = 30).map { it.toTradeAsset() }
        val tradesRealTime = tradeFactory.buildList(number = 2).map { it.toTradeAsset() }
        mockGetTradesAsset(tradesAsset = trades)
        mockGetRealTimeTradeAsset(tradesAsset = tradesRealTime)
        val expectedTradesData = (tradesRealTime + trades).take(Constants.DEFAULT_LIMIT_TRADES_ASSET)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = detailViewModel.uiState.value
        assertThat(uiState.latestTrades).isEqualTo(expectedTradesData)
        coVerify(exactly = 1) { getRealTimeTradesAssetUseCase(symbol = asset.symbol, typeAsset = asset.getTypeAsset()) }
    }

    @Test
    fun `When Refresh, Then Success on get all data`() = runTest {
        // GIVEN
        val trades = tradeFactory.buildList(number = 30).map { it.toTradeAsset() }
        mockGetTradesAsset(tradesAsset = trades)
        val quotes = quotesFactory.buildList(number = 30).map { it.toQuoteAsset() }
        mockGetQuotesAsset(quotesAsset = quotes)
        val bars = barAssetFactory.buildList(number = 30).map { it.toBarAsset() }
        mockGetBarsAsset(barAsset = bars)
        initViewModel()
        advanceUntilIdle()

        // WHEN
        detailViewModel.onEvent(event = DetailEvent.Refresh)

        // THEN
        detailViewModel.uiState.test {
            skipItems(1)
            assertThat(awaitItem().isRefreshing).isTrue()
            cancelAndConsumeRemainingEvents()
        }
        advanceUntilIdle()
        val uiState = detailViewModel.uiState.value
        assertThat(uiState.latestTrades).isEqualTo(trades)
        assertThat(uiState.latestQuotes).isEqualTo(quotes)
        assertThat(uiState.assetCharInfo.barsInfo).isEqualTo(bars)
        assertThat(uiState.isRefreshing).isFalse()
        coVerify(exactly = 2) { getTradesAssetUseCase(symbol = asset.symbol, typeAsset = asset.getTypeAsset(), startDate = any()) }
        coVerify(exactly = 2) { getQuoteAsset(symbol = asset.symbol, typeAsset = asset.getTypeAsset(), startDate = any()) }
        coVerify(exactly = 2) {
            getBarsAssetUseCase(
                symbol = asset.symbol, typeAsset = asset.getTypeAsset(), startDate = any(), endDate = any(),
                limit = any(), sort = any(), timeFrame = any(),
            )
        }
    }

    @Test
    fun `When Refresh while is refreshing, Then don't make request again`() = runTest {
        // GIVEN
        val trades = tradeFactory.buildList(number = 30).map { it.toTradeAsset() }
        mockGetTradesAsset(tradesAsset = trades)
        val quotes = quotesFactory.buildList(number = 30).map { it.toQuoteAsset() }
        mockGetQuotesAsset(quotesAsset = quotes)
        val bars = barAssetFactory.buildList(number = 30).map { it.toBarAsset() }
        mockGetBarsAsset(barAsset = bars)
        initViewModel()
        advanceUntilIdle()

        // WHEN
        detailViewModel.onEvent(event = DetailEvent.Refresh)
        detailViewModel.onEvent(event = DetailEvent.Refresh)
        advanceUntilIdle()

        // THEN
        val uiState = detailViewModel.uiState.value
        assertThat(uiState.isRefreshing).isFalse()
        coVerify(exactly = 2) { getTradesAssetUseCase(symbol = asset.symbol, typeAsset = asset.getTypeAsset(), startDate = any()) }
        coVerify(exactly = 2) { getQuoteAsset(symbol = asset.symbol, typeAsset = asset.getTypeAsset(), startDate = any()) }
        coVerify(exactly = 2) {
            getBarsAssetUseCase(
                symbol = asset.symbol, typeAsset = asset.getTypeAsset(), startDate = any(), endDate = any(),
                limit = any(), sort = any(), timeFrame = any(),
            )
        }
    }

    @Test
    fun `When ChangeFilterAssetDetailInfo, Then update selectedFilterDetailInfo`() = runTest {
        // GIVEN
        initViewModel()
        advanceUntilIdle()

        // WHEN
        var uiState = detailViewModel.uiState.value
        val filterToSelect = uiState.filtersAssetDetailInfo.last()
        detailViewModel.onEvent(event = DetailEvent.ChangeFilterAssetDetailInfo(newFilter = filterToSelect))
        advanceUntilIdle()

        // THEN
        uiState = detailViewModel.uiState.value
        assertThat(uiState.selectedFilterDetailInfo).isEqualTo(filterToSelect)
    }

    @Test
    fun `When ChangeFilterAssetChart, Then update selectedFilterHistorical`() = runTest {
        // GIVEN
        initViewModel()
        advanceUntilIdle()

        // WHEN
        var uiState = detailViewModel.uiState.value
        val filterToSelect = uiState.filtersHistoricalBar.last()
        detailViewModel.onEvent(event = DetailEvent.ChangeFilterAssetChart(newFilter = filterToSelect))
        advanceUntilIdle()

        // THEN
        uiState = detailViewModel.uiState.value
        assertThat(uiState.selectedFilterHistorical).isEqualTo(filterToSelect)
        coVerify {
            getBarsAssetUseCase.invoke(
                symbol = any(), typeAsset = any(), timeFrame = filterToSelect.timeFrameIntervalValues,
                startDate = any(), endDate = any(),
            )
        }
    }

    @Test
    fun `When ChangeFilterAssetChart when newFilter is equal to selected, Then ignore call`() = runTest {
        // GIVEN
        initViewModel()
        advanceUntilIdle()

        // WHEN
        var uiState = detailViewModel.uiState.value
        val filterToSelect = uiState.selectedFilterHistorical
        detailViewModel.onEvent(event = DetailEvent.ChangeFilterAssetChart(newFilter = filterToSelect))
        advanceUntilIdle()

        // THEN
        uiState = detailViewModel.uiState.value
        assertThat(uiState.selectedFilterHistorical).isEqualTo(filterToSelect)
        coVerify(exactly = 1) {
            getBarsAssetUseCase.invoke(
                symbol = any(), typeAsset = any(), timeFrame = any(),
                startDate = any(), endDate = any(),
            )
        }
    }

    @Test
    fun `When ChangeFilterAssetChart but request is running, Then ignore cancel actual request and make a new`() = runTest {
        // GIVEN
        initViewModel()
        advanceUntilIdle()
        mockGetBarsAssetWithDelay(barAsset = emptyList())

        // WHEN
        var uiState = detailViewModel.uiState.value
        val filters = uiState.filtersHistoricalBar
        detailViewModel.onEvent(event = DetailEvent.ChangeFilterAssetChart(newFilter = filters.last()))
        detailViewModel.onEvent(event = DetailEvent.ChangeFilterAssetChart(newFilter = filters.first()))
        detailViewModel.onEvent(event = DetailEvent.ChangeFilterAssetChart(newFilter = filters.last()))
        detailViewModel.onEvent(event = DetailEvent.ChangeFilterAssetChart(newFilter = filters.first()))
        advanceUntilIdle()

        // THEN
        uiState = detailViewModel.uiState.value
        assertThat(uiState.selectedFilterHistorical).isEqualTo(filters.first())
        coVerify(exactly = 2) {
            getBarsAssetUseCase.invoke(
                symbol = any(), typeAsset = any(), timeFrame = any(),
                startDate = any(), endDate = any(),
            )
        }
    }

    @Test
    fun `When ChangeActualPriceToShow, Then update current price to show`() = runTest {
        // GIVEN
        val newPrice = 180.0
        val valueToCompare = 80.0
        val expectedPercentage = abs(((newPrice - valueToCompare) / valueToCompare) * 100)
        initViewModel()
        advanceUntilIdle()

        // WHEN
        detailViewModel.onEvent(event = DetailEvent.ChangeActualPriceToShow(priceToShow = newPrice, valueToCompare = valueToCompare))
        advanceUntilIdle()

        // THEN
        val uiState = detailViewModel.uiState.value
        assertThat(uiState.currentPriceInfo.price).isEqualTo(newPrice)
        assertThat(uiState.currentPriceInfo.percentage).isEqualTo(expectedPercentage)
        assertThat(uiState.currentPriceInfo.priceDifference).isEqualTo(newPrice-valueToCompare)
    }

    private fun mockResponseGetAssetById(asset: Asset?) {
        coEvery {
            getAssetByIdUseCase.invoke(id = any())
        } returns if (asset == null) Resource.Error(message = "Error on GetAssetById")
        else Resource.Success(data = asset)
    }

    private fun mockResponseGetAssetByIdRetry(asset: Asset) {
        coEvery {
            getAssetByIdUseCase.invoke(id = any())
        } returnsMany listOf(Resource.Error(message = "Error on GetAssetById"),Resource.Success(data = asset))
    }

    private fun mockSubscribeRealTimeAsset(message: SubscriptionMessage?) {
        coEvery {
            setSubscribeRealTimeAssetUseCase.invoke(symbol = any(), typeAsset = any())
        } returns if (message == null) Resource.Error(message = "Error on Subscribe")
        else Resource.Success(data = message)
    }

    private fun mockRetrySubscribeRealTimeAsset(message: SubscriptionMessage) {
        coEvery {
            setSubscribeRealTimeAssetUseCase.invoke(symbol = any(), typeAsset = any())
        } returnsMany listOf(Resource.Error(message = "Error on Subscribe"), Resource.Success(data = message))
    }

    private fun mockUnSubscribeRealTimeAsset(message: SubscriptionMessage?) {
        coEvery {
            setUnsubscribeRealTimeAssetUseCase.invoke(symbol = any(), typeAsset = any())
        } returns if (message == null) Resource.Error(message = "Error on UnSubscribe")
        else Resource.Success(data = message)
    }

    private fun mockGetLatestBarAsset(barAsset: BarAsset?) {
        coEvery {
            getLatestBarAssetUseCase.invoke(symbol = any(), typeAsset = any())
        } returns if (barAsset == null) Resource.Error(message = "Error on Latest Bar Asset")
        else Resource.Success(data = barAsset)
    }

    private fun mockGetLatestBarAssetRetry(barAsset: BarAsset) {
        coEvery {
            getLatestBarAssetUseCase.invoke(symbol = any(), typeAsset = any())
        } returnsMany(listOf(Resource.Error(message = "Error on Latest Bar Asset"),Resource.Success(data = barAsset)))
    }

    private fun mockGetBarsAsset(barAsset: List<BarAsset>?) {
        coEvery {
            getBarsAssetUseCase.invoke(symbol = any(), typeAsset = any(), startDate = any(), endDate = any(), timeFrame = any())
        } returns if (barAsset == null) Resource.Error(message = "Error on Bars Asset")
        else Resource.Success(data = barAsset)
    }

    private fun mockGetBarsAssetWithDelay(barAsset: List<BarAsset>) {
        coEvery {
            getBarsAssetUseCase.invoke(symbol = any(), typeAsset = any(), startDate = any(), endDate = any(), timeFrame = any())
        } coAnswers {
            delay(500)
            Resource.Success(data = barAsset)
        }
    }

    private fun mockRetryGetBarsAsset(barAsset: List<BarAsset>) {
        coEvery {
            getBarsAssetUseCase.invoke(symbol = any(), typeAsset = any(), startDate = any(), endDate = any(), timeFrame = any())
        } returnsMany listOf(Resource.Error(message = "Error on Bars Asset"),Resource.Success(data = barAsset))
    }

    private fun mockGetRealTimeBarsAsset(barAsset: List<BarAsset>) {
        coEvery {
            getRealTimeBarAsset.invoke(typeAsset = any(), symbol = any())
        } returns flow {
            emit(barAsset)
        }
    }

    private fun mockGetQuotesAsset(quotesAsset: List<QuoteAsset>?) {
        coEvery {
            getQuoteAsset.invoke(
                symbol = any(), typeAsset = any(), startDate = any(), endDate = any(),
                sort = any(), limit = any(), pageToken = any(),
            )
        } returns if (quotesAsset == null) Resource.Error(message = "Error on Quotes Asset")
        else Resource.Success(data = QuotesResponse(quotes = quotesAsset))
    }

    private fun mockRetryGetQuotesAsset(quotesAsset: List<QuoteAsset>) {
        coEvery {
            getQuoteAsset.invoke(
                symbol = any(), typeAsset = any(), startDate = any(), endDate = any(),
                sort = any(), limit = any(), pageToken = any(),
            )
        } returnsMany listOf(Resource.Error(message = "Error on Quotes Asset"),Resource.Success(data = QuotesResponse(quotes = quotesAsset)))
    }

    private fun mockGetRealTimeQuoteAsset(quotesAsset: List<QuoteAsset>) {
        coEvery {
            getRealTimeQuotesAssetUseCase.invoke(typeAsset = any(), symbol = any())
        } returns flow {
            emit(quotesAsset)
        }
    }

    private fun mockGetTradesAsset(tradesAsset: List<TradeAsset>?) {
        coEvery {
            getTradesAssetUseCase.invoke(
                symbol = any(), typeAsset = any(), startDate = any(), endDate = any(),
                sort = any(), limit = any(), pageToken = any(),
            )
        } returns if (tradesAsset == null) Resource.Error(message = "Error on Trades Asset")
        else Resource.Success(data = TradesResponse(trades = tradesAsset))
    }

    private fun mockRetryGetTradesAsset(tradesAsset: List<TradeAsset>) {
        coEvery {
            getTradesAssetUseCase.invoke(
                symbol = any(), typeAsset = any(), startDate = any(), endDate = any(),
                sort = any(), limit = any(), pageToken = any(),
            )
        } returnsMany listOf(Resource.Error(message = "Error on Trades Asset"),Resource.Success(data = TradesResponse(trades = tradesAsset)))
    }

    private fun mockGetRealTimeTradeAsset(tradesAsset: List<TradeAsset>) {
        coEvery {
            getRealTimeTradesAssetUseCase.invoke(typeAsset = any(), symbol = any())
        } returns flow {
            emit(tradesAsset)
        }
    }

    private fun mockStatusServiceAsset() {
        coEvery {
            getStatusServiceAssetUseCase.invoke(typeAsset = any())
        } returns flow {
            delay(500)
            emit(WebSocket.Event.OnConnectionOpened(webSocket = Any()))
        }
    }
}