package dev.pinkroom.marketsight.data.data_source

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.google.gson.Gson
import com.tinder.scarlet.Message
import com.tinder.scarlet.WebSocket
import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.HelperIdentifierMessagesAlpacaService
import dev.pinkroom.marketsight.data.remote.AlpacaCryptoApi
import dev.pinkroom.marketsight.data.remote.AlpacaService
import dev.pinkroom.marketsight.data.remote.AlpacaStockApi
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.BarsResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.QuotesResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.TradesResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_crypto_api.BarsCryptoResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_crypto_api.QuotesCryptoResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_crypto_api.TradesCryptoResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.ErrorMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.SubscriptionMessageDto
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.factories.BarAssetDtoFactory
import dev.pinkroom.marketsight.factories.QuoteAssetDtoFactory
import dev.pinkroom.marketsight.factories.TradeAssetDtoFactory
import dev.pinkroom.marketsight.util.MainCoroutineRule
import dev.pinkroom.marketsight.util.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class MarketRemoteDataSourceTest{
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val gson = Gson()
    private val barAssetDtoFactory = BarAssetDtoFactory()
    private val tradeAssetDtoFactory = TradeAssetDtoFactory()
    private val quoteAssetDtoFactory = QuoteAssetDtoFactory()
    private val dispatchers = TestDispatcherProvider()
    private val alpacaStockApi = mockk<AlpacaStockApi>()
    private val alpacaCryptoApi = mockk<AlpacaCryptoApi>()
    private val alpacaServiceStock = mockk<AlpacaService>(relaxed = true, relaxUnitFun = true)
    private val alpacaServiceCrypto = mockk<AlpacaService>(relaxed = true, relaxUnitFun = true)
    private val marketRemoteDataSource = MarketRemoteDataSource(
        alpacaStockApi = alpacaStockApi,
        gson = gson,
        alpacaCryptoApi = alpacaCryptoApi,
        dispatchers = dispatchers,
        alpacaServiceStock = alpacaServiceStock,
        alpacaServiceCrypto = alpacaServiceCrypto,
    )

    @Test
    fun `When getBars on asset of type Stock, Then return a list of BarAssetDto`() = runTest {
        // GIVEN
        val type = TypeAsset.Stock
        val symbol = "AAPL"
        mockGetBarsApi(symbol = symbol)

        // WHEN
        val response = marketRemoteDataSource.getBars(
            symbol = symbol, typeAsset = type,
            startDate = LocalDateTime.now().minusDays(1), endDate = LocalDateTime.now(),
        )

        // THEN
        coVerify {
            alpacaStockApi.getHistoricalBarsStock(
                symbol = symbol,
                endDate = any(), startDate = any(),
                limit = any(), timeFrame = any(),
                sort = any(), feed = any(),
            )
        }
        assertThat(response).isNotEmpty()
    }

    @Test
    fun `When getBars on asset of type Crypto, Then return a list of BarAssetDto`() = runTest {
        // GIVEN
        val type = TypeAsset.Crypto
        val symbol = "TSLA"
        mockGetBarsApi(symbol = symbol)

        // WHEN
        val response = marketRemoteDataSource.getBars(
            symbol = symbol, typeAsset = type,
            startDate = LocalDateTime.now().minusDays(1), endDate = LocalDateTime.now(),
        )

        // THEN
        coVerify {
            alpacaCryptoApi.getHistoricalBarsCrypto(
                symbol = symbol,
                endDate = any(), startDate = any(),
                limit = any(), timeFrame = any(),
                sort = any(),
            )
        }
        assertThat(response).isNotEmpty()
    }

    @Test
    fun `When getRealTimeBars on asset of type Stock, Then return a list of BarAssetDto`() = runTest {
        // GIVEN
        val type = TypeAsset.Stock
        val symbol = "TSLA"
        mockGetRealTimeBarsService(symbol = symbol)

        // WHEN
        val response = marketRemoteDataSource.getRealTimeBars(typeAsset = type).last()

        // THEN
        coVerify {
            alpacaServiceStock.observeResponse()
        }
        assertThat(response).isNotEmpty()
        response.map {
            assertThat(it.symbol).isEqualTo(symbol)
        }
    }

    @Test
    fun `When getRealTimeBars on asset of type Crypto, Then return a list of BarAssetDto`() = runTest {
        // GIVEN
        val type = TypeAsset.Crypto
        val symbol = "AAPL"
        mockGetRealTimeBarsService(symbol = symbol)

        // WHEN
        val response = marketRemoteDataSource.getRealTimeBars(typeAsset = type).last()

        // THEN
        coVerify {
            alpacaServiceCrypto.observeResponse()
        }
        assertThat(response).isNotEmpty()
        response.map {
            assertThat(it.symbol).isEqualTo(symbol)
        }
    }

    @Test
    fun `When getTrades on asset of type Stock, Then return a TradesResponseDto`() = runTest {
        // GIVEN
        val type = TypeAsset.Stock
        val symbol = "AAPL"
        mockGetTradesApi(symbol = symbol)

        // WHEN
        val response = marketRemoteDataSource.getTrades(symbol = symbol, typeAsset = type)

        // THEN
        coVerify {
            alpacaStockApi.getHistoricalTradesStock(
                symbol = symbol,
                endDate = any(), startDate = any(),
                limit = any(), sort = any(),
                pageToken = any(), feed = any(),
            )
        }
        assertThat(response.symbol).isEqualTo(symbol)
        assertThat(response.trades).isNotNull()
    }

    @Test
    fun `When getTrades on asset of type Crypto, Then return a TradesResponseDto`() = runTest {
        // GIVEN
        val type = TypeAsset.Crypto
        val symbol = "TSLA"
        mockGetTradesApi(symbol = symbol)

        // WHEN
        val response = marketRemoteDataSource.getTrades(symbol = symbol, typeAsset = type)

        // THEN
        coVerify {
            alpacaCryptoApi.getHistoricalTradesCrypto(
                symbol = symbol,
                endDate = any(), startDate = any(),
                limit = any(), sort = any(),
                pageToken = any(),
            )
        }
        assertThat(response.symbol).isEqualTo(symbol)
        assertThat(response.trades).isNotNull()
    }

    @Test
    fun `When getRealTimeTrades on asset of type Stock, Then return a List of TradeAssetDto`() = runTest {
        // GIVEN
        val type = TypeAsset.Stock
        val symbol = "TSLA"
        mockGetRealTimeTradesService(symbol = symbol)

        // WHEN
        val response = marketRemoteDataSource.getRealTimeTrades(typeAsset = type).last()

        // THEN
        coVerify {
            alpacaServiceStock.observeResponse()
        }
        assertThat(response).isNotEmpty()
        response.forEach {
            assertThat(it.symbol).isEqualTo(symbol)
        }
    }

    @Test
    fun `When getRealTimeTrades on asset of type Crypto, Then return a List of TradeAssetDto`() = runTest {
        // GIVEN
        val type = TypeAsset.Crypto
        val symbol = "BTC"
        mockGetRealTimeTradesService(symbol = symbol)

        // WHEN
        val response = marketRemoteDataSource.getRealTimeTrades(typeAsset = type).last()

        // THEN
        coVerify {
            alpacaServiceCrypto.observeResponse()
        }
        assertThat(response).isNotEmpty()
        response.forEach {
            assertThat(it.symbol).isEqualTo(symbol)
        }
    }

    @Test
    fun `When getQuotes on asset of type Stock, Then return a QuotesResponseDto`() = runTest {
        // GIVEN
        val type = TypeAsset.Stock
        val symbol = "AAPL"
        mockGetQuotesApi(symbol = symbol)

        // WHEN
        val response = marketRemoteDataSource.getQuotes(symbol = symbol, typeAsset = type)

        // THEN
        coVerify {
            alpacaStockApi.getHistoricalQuotesStock(
                symbol = symbol,
                endDate = any(), startDate = any(),
                limit = any(), sort = any(),
                pageToken = any(), feed = any(),
            )
        }
        assertThat(response.symbol).isEqualTo(symbol)
        assertThat(response.quotes).isNotNull()
    }

    @Test
    fun `When getQuotes on asset of type Crypto, Then return a QuotesResponseDto`() = runTest {
        // GIVEN
        val type = TypeAsset.Crypto
        val symbol = "BTC"
        mockGetQuotesApi(symbol = symbol)

        // WHEN
        val response = marketRemoteDataSource.getQuotes(symbol = symbol, typeAsset = type)

        // THEN
        coVerify {
            alpacaCryptoApi.getHistoricalQuotesCrypto(
                symbol = symbol,
                endDate = any(), startDate = any(),
                limit = any(), sort = any(),
                pageToken = any(),
            )
        }
        assertThat(response.symbol).isEqualTo(symbol)
        assertThat(response.quotes).isNotNull()
    }

    @Test
    fun `When getRealTimeQuotes on asset of type Stock, Then return a List of QuoteAssetDto`() = runTest {
        // GIVEN
        val type = TypeAsset.Stock
        val symbol = "TSLA"
        mockGetRealTimeQuotesService(symbol = symbol)

        // WHEN
        val response = marketRemoteDataSource.getRealTimeQuotes(typeAsset = type).last()

        // THEN
        coVerify {
            alpacaServiceStock.observeResponse()
        }
        assertThat(response).isNotEmpty()
        response.forEach {
            assertThat(it.symbol).isEqualTo(symbol)
        }
    }

    @Test
    fun `When getRealTimeQuotes on asset of type Crypto, Then return a List of QuoteAssetDto`() = runTest {
        // GIVEN
        val type = TypeAsset.Crypto
        val symbol = "BTC"
        mockGetRealTimeQuotesService(symbol = symbol)

        // WHEN
        val response = marketRemoteDataSource.getRealTimeQuotes(typeAsset = type).last()

        // THEN
        coVerify {
            alpacaServiceCrypto.observeResponse()
        }
        assertThat(response).isNotEmpty()
        response.forEach {
            assertThat(it.symbol).isEqualTo(symbol)
        }
    }

    @Test
    fun `When statusService, Then return a WebSocket Event`() = runTest {
        // GIVEN
        mockObserveOnConnectionEvent()

        // WHEN
        val response = marketRemoteDataSource.statusService(
            typeAsset = TypeAsset.Stock,
        ).toList()

        // THEN
        coVerify {
            alpacaServiceStock.observeOnConnectionEvent()
        }
        assertThat(response).isNotEmpty()
    }

    @Test
    fun `When subscribeUnsubscribeRealTimeFinancialData on asset, Then return a SubscriptionMessageDto`() = runTest {
        // GIVEN
        val type = TypeAsset.Stock
        val symbol = "TSLA"
        mockResponseSubscribeUnsubscribeSymbolRealTime(symbol = symbol)

        // WHEN
        val response = mutableListOf<SubscriptionMessageDto>()
        launch {
            marketRemoteDataSource.subscribeUnsubscribeRealTimeFinancialData(
                typeAsset = type,
                action = ActionAlpaca.Subscribe,
                symbol = symbol
            ).toList(response)
        }.join()

        // THEN
        coVerify {
            alpacaServiceStock.observeResponse()
        }
        assertThat(response).isNotEmpty()
        assertThat(response.size).isEqualTo(1)
        assertThat(response.first().type).isEqualTo(HelperIdentifierMessagesAlpacaService.Subscription.identifier)
    }

    @Test
    fun `When subscribeUnsubscribeRealTimeFinancialData on asset, Then throw error`() = runTest {
        // GIVEN
        val type = TypeAsset.Stock
        val symbol = "TSLA"
        mockResponseSubscribeUnsubscribeSymbolRealTime(isToSendError = true)

        // WHEN
        val response = mutableListOf<SubscriptionMessageDto>()
        assertFailure {
            marketRemoteDataSource.subscribeUnsubscribeRealTimeFinancialData(
                typeAsset = type,
                action = ActionAlpaca.Unsubscribe,
                symbol = symbol,
            ).toList(response)
        }.hasMessage("Error on Subscription")

        // THEN
        coVerify {
            alpacaServiceStock.observeResponse()
        }
    }

    private fun mockResponseSubscribeUnsubscribeSymbolRealTime(symbol: String? = null, isToSendError: Boolean = false) {
        val symbols = symbol?.let { listOf(symbol) } ?: run { emptyList() }
        val returnedMessageService = listOf(
            gson.toJsonTree(
                SubscriptionMessageDto(
                    type = "subscription",
                    bars = symbols,
                    quotes = symbols,
                    trades = symbols,
                )
            )
        )
        val errorMessage = listOf(
            gson.toJsonTree(
                ErrorMessageDto(
                    type = "error",
                    msg = "Not Found",
                    code = 404,
                )
            )
        )

        coEvery {
            alpacaServiceStock.observeResponse()
        }.returns(
            flow {
                val listBar = buildMessageResponseServiceBar(symbol = symbol ?: "TSLA")
                emit(listBar)
                emit(listBar)
                if (isToSendError) emit(errorMessage)
                else emit(returnedMessageService)
            }
        )

        coEvery {
            alpacaServiceCrypto.observeResponse()
        }.returns(
            flow {
                val listBar = buildMessageResponseServiceBar(symbol = symbol ?: "BTC")
                emit(listBar)
                emit(listBar)
                if (isToSendError) emit(errorMessage)
                else emit(returnedMessageService)
            }
        )
    }

    private fun mockObserveOnConnectionEvent(){
        coEvery {
            alpacaServiceStock.observeOnConnectionEvent()
        }.returns(
            flow {
                emit(WebSocket.Event.OnConnectionOpened(webSocket = Any()))
                emit(WebSocket.Event.OnMessageReceived(message = Message.Text(value = "TEST")))
            }
        )
    }

    private fun mockGetBarsApi(symbol: String) {
        coEvery {
            alpacaCryptoApi.getHistoricalBarsCrypto(
                symbol = any(),
                endDate = any(), startDate = any(),
                limit = any(), timeFrame = any(),
                sort = any(),
            )
        }.returns(
            BarsCryptoResponseDto(
                bars = barAssetDtoFactory.buildList(
                    number = 20, symbol = symbol,
                ).groupBy { it.symbol!! },
                nextPageToken = null,
            )
        )

        coEvery {
            alpacaStockApi.getHistoricalBarsStock(
                symbol = any(),
                endDate = any(), startDate = any(),
                limit = any(), timeFrame = any(),
                sort = any(), feed = any()
            )
        }.returns(
            BarsResponseDto(
                bars = barAssetDtoFactory.buildList(number = 10),
                symbol = symbol,
                nextPageToken = null,
            )
        )
    }

    private fun mockGetRealTimeBarsService(symbol: String) {
        coEvery {
            alpacaServiceStock.observeResponse()
        }.returns(
            flow {
                val listBar = buildMessageResponseServiceBar(symbol = symbol)
                emit(listBar)
            }
        )

        coEvery {
            alpacaServiceCrypto.observeResponse()
        }.returns(
            flow {
                val listBar = buildMessageResponseServiceBar(symbol = symbol)
                emit(listBar)
            }
        )
    }

    private fun mockGetTradesApi(symbol: String) {
        coEvery {
            alpacaStockApi.getHistoricalTradesStock(
                symbol = any(),
                endDate = any(), startDate = any(),
                limit = any(), sort = any(),
                feed = any(), pageToken = any(),
            )
        }.returns(
            TradesResponseDto(
                trades = tradeAssetDtoFactory.buildList(number = 20),
                pageToken = null,
                symbol = symbol,
            )
        )

        coEvery {
            alpacaCryptoApi.getHistoricalTradesCrypto(
                symbol = any(),
                endDate = any(), startDate = any(),
                limit = any(), sort = any(),
                pageToken = any(),
            )
        }.returns(
            TradesCryptoResponseDto(
                trades = tradeAssetDtoFactory.buildList(number = 20, symbol = symbol).groupBy { it.symbol!! },
                pageToken = null,
            )
        )
    }

    private fun mockGetRealTimeTradesService(symbol: String) {
        coEvery {
            alpacaServiceStock.observeResponse()
        }.returns(
            flow {
                val listTrade = buildMessageResponseServiceTrade(symbol = symbol)
                emit(listTrade)
            }
        )

        coEvery {
            alpacaServiceCrypto.observeResponse()
        }.returns(
            flow {
                val listTrade = buildMessageResponseServiceTrade(symbol = symbol)
                emit(listTrade)
            }
        )
    }

    private fun mockGetQuotesApi(symbol: String) {
        coEvery {
            alpacaStockApi.getHistoricalQuotesStock(
                symbol = any(),
                endDate = any(), startDate = any(),
                limit = any(), sort = any(),
                feed = any(), pageToken = any(),
            )
        }.returns(
            QuotesResponseDto(
                quotes = quoteAssetDtoFactory.buildList(number = 20),
                pageToken = null,
                symbol = symbol,
            )
        )

        coEvery {
            alpacaCryptoApi.getHistoricalQuotesCrypto(
                symbol = any(),
                endDate = any(), startDate = any(),
                limit = any(), sort = any(),
                pageToken = any(),
            )
        }.returns(
            QuotesCryptoResponseDto(
                quotes = quoteAssetDtoFactory.buildList(number = 20, symbol = symbol).groupBy { it.symbol!! },
                pageToken = null,
            )
        )
    }

    private fun mockGetRealTimeQuotesService(symbol: String) {
        coEvery {
            alpacaServiceStock.observeResponse()
        }.returns(
            flow {
                val listQuotes = buildMessageResponseServiceQuote(symbol = symbol)
                emit(listQuotes)
            }
        )

        coEvery {
            alpacaServiceCrypto.observeResponse()
        }.returns(
            flow {
                val listQuotes = buildMessageResponseServiceQuote(symbol = symbol)
                emit(listQuotes)
            }
        )
    }

    private fun buildMessageResponseServiceBar(symbol: String) = barAssetDtoFactory.buildList(
        number = 20, symbol = symbol,
        type = HelperIdentifierMessagesAlpacaService.Bars.identifier,
    ).map { gson.toJsonTree(it) }

    private fun buildMessageResponseServiceTrade(symbol: String) = tradeAssetDtoFactory.buildList(
        number = 20, symbol = symbol,
        type = HelperIdentifierMessagesAlpacaService.Trades.identifier,
    ).map { gson.toJsonTree(it) }

    private fun buildMessageResponseServiceQuote(symbol: String) = quoteAssetDtoFactory.buildList(
        number = 20, symbol = symbol,
        type = HelperIdentifierMessagesAlpacaService.Quotes.identifier,
    ).map { gson.toJsonTree(it) }
}