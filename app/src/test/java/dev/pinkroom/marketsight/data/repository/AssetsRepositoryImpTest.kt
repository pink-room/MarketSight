package dev.pinkroom.marketsight.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import com.tinder.scarlet.Message
import com.tinder.scarlet.WebSocket
import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.HelperIdentifierMessagesAlpacaWS
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.data.data_source.AssetsLocalDataSource
import dev.pinkroom.marketsight.data.data_source.AssetsRemoteDataSource
import dev.pinkroom.marketsight.data.data_source.MarketRemoteDataSource
import dev.pinkroom.marketsight.data.mapper.toAsset
import dev.pinkroom.marketsight.data.mapper.toAssetEntity
import dev.pinkroom.marketsight.data.mapper.toBarAsset
import dev.pinkroom.marketsight.data.mapper.toQuoteAsset
import dev.pinkroom.marketsight.data.mapper.toTradeAsset
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.BarAssetDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.QuoteAssetDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.QuotesResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.TradeAssetDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.TradesResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.SubscriptionMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_paper_api.AssetDto
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.factories.AssetDtoFactory
import dev.pinkroom.marketsight.factories.BarAssetDtoFactory
import dev.pinkroom.marketsight.factories.QuoteAssetDtoFactory
import dev.pinkroom.marketsight.factories.TradeAssetDtoFactory
import dev.pinkroom.marketsight.util.MainCoroutineRule
import dev.pinkroom.marketsight.util.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class AssetsRepositoryImpTest{
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val assetDtoFactory = AssetDtoFactory()
    private val barAssetDtoFactory = BarAssetDtoFactory()
    private val tradeAssetDtoFactory = TradeAssetDtoFactory()
    private val quoteAssetDtoFactory = QuoteAssetDtoFactory()
    private val dispatchers = TestDispatcherProvider()
    private val assetsRemoteDataSource = mockk<AssetsRemoteDataSource>()
    private val assetsLocalDataSource = mockk<AssetsLocalDataSource>()
    private val marketRemoteDataSource = mockk<MarketRemoteDataSource>()
    private val assetsRepository = AssetsRepositoryImp(
        assetsRemoteDataSource = assetsRemoteDataSource,
        marketRemoteDataSource = marketRemoteDataSource,
        assetsLocalDataSource = assetsLocalDataSource,
        dispatchers = dispatchers,
    )

    @Test
    fun `When getAllAssets of type Stock, Then on Success return a List of Asset`() = runTest {
        // GIVEN
        val typeAsset = TypeAsset.Stock
        val assetsDto = assetDtoFactory.listAssets(number = 250, type = typeAsset)
        mockResponseGetAssetsAssetsRemoteDataSource(
            assetsToReturn = assetsDto,
        )

        // WHEN
        val response = assetsRepository.getAllAssets(typeAsset = typeAsset, fetchFromRemote = true)

        // THEN
        val expectedResponse = assetsDto.map { it.toAsset() }
        assertThat(response).isInstanceOf(Resource.Success::class)
        assertThat((response as Resource.Success).data).isEqualTo(expectedResponse)
    }

    @Test
    fun `When getAllAssets cached of type Stock, Then on Success return a List of Asset`() = runTest {
        // GIVEN
        val typeAsset = TypeAsset.Stock
        val assetsDto = assetDtoFactory.listAssets(number = 250, type = typeAsset)
        mockResponseGetAssetsAssetsRemoteDataSource(
            assetsToReturn = assetsDto,
            typeAsset = typeAsset,
        )

        // WHEN
        val response = assetsRepository.getAllAssets(typeAsset = typeAsset, fetchFromRemote = false)

        // THEN
        val expectedResponse = assetsDto.map { it.toAsset() }
        assertThat(response).isInstanceOf(Resource.Success::class)
        assertThat((response as Resource.Success).data).isEqualTo(expectedResponse)
    }

    @Test
    fun `When getAllAssets of type Stock, Then on Error return a Resource Error`() = runTest {
        // GIVEN
        val typeAsset = TypeAsset.Stock
        val errorMessage = "Error"

        mockResponseGetAssetsAssetsRemoteDataSource(
            messageError = errorMessage,
            isToThrowError = true,
        )

        // WHEN
        val response = assetsRepository.getAllAssets(typeAsset = typeAsset, fetchFromRemote = true)

        // THEN
        assertThat(response).isInstanceOf(Resource.Error::class)
        assertThat((response as Resource.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `When getBars of type Stock, Then on Success return a List of BarAsset`() = runTest {
        // GIVEN
        val typeAsset = TypeAsset.Stock
        val symbol = "TSLA"
        val barsDto = barAssetDtoFactory.buildList(number = 10)
        mockResponseGetBarsMarketRemoteDataSource(
            barsToReturn = barsDto,
        )

        // WHEN
        val response = assetsRepository.getBars(
            symbol = symbol, typeAsset = typeAsset,
            endDate = LocalDateTime.now(), startDate = LocalDateTime.now().minusDays(1),
        )

        // THEN
        val expectedResponse = barsDto.map { it.toBarAsset() }
        assertThat(response).isInstanceOf(Resource.Success::class)
        assertThat((response as Resource.Success).data).isEqualTo(expectedResponse)
    }

    @Test
    fun `When getBars of type Stock, Then on Error return a Resource Error`() = runTest {
        // GIVEN
        val typeAsset = TypeAsset.Stock
        val errorMessage = "Error"
        val symbol = "TSLA"
        mockResponseGetBarsMarketRemoteDataSource(
            messageError = errorMessage,
            isToThrowError = true,
        )

        // WHEN
        val response = assetsRepository.getBars(
            symbol = symbol, typeAsset = typeAsset,
            startDate = LocalDateTime.now().minusDays(1), endDate = LocalDateTime.now(),
        )

        // THEN
        assertThat(response).isInstanceOf(Resource.Error::class)
        assertThat((response as Resource.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `When getTrades of type Stock, Then on Success return a TradeResponse`() = runTest {
        // GIVEN
        val typeAsset = TypeAsset.Stock
        val symbol = "TSLA"
        val tradesDto = tradeAssetDtoFactory.buildList(number = 10)
        mockResponseGetTradesMarketRemoteDataSource(
            tradesToReturn = tradesDto,
            symbol = symbol,
        )

        // WHEN
        val response = assetsRepository.getTrades(
            symbol = symbol, typeAsset = typeAsset,
            endDate = LocalDateTime.now(), startDate = LocalDateTime.now().minusDays(1),
        )

        // THEN
        val expectedResponse = tradesDto.map { it.toTradeAsset() }
        assertThat(response).isInstanceOf(Resource.Success::class)
        (response as Resource.Success).data.trades.forEachIndexed { i, trade ->
            assertThat(trade.tradePrice).isEqualTo(expectedResponse[i].tradePrice)
        }
    }

    @Test
    fun `When getTrades of type Stock, Then on Error return a Resource Error`() = runTest {
        // GIVEN
        val typeAsset = TypeAsset.Stock
        val errorMessage = "Error"
        val symbol = "TSLA"
        mockResponseGetTradesMarketRemoteDataSource(
            isToThrowError = true,
            messageError = errorMessage,
            symbol = symbol,
        )

        // WHEN
        val response = assetsRepository.getTrades(
            symbol = symbol, typeAsset = typeAsset,
            startDate = LocalDateTime.now().minusDays(1), endDate = LocalDateTime.now(),
        )

        // THEN
        assertThat(response).isInstanceOf(Resource.Error::class)
        assertThat((response as Resource.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `When getQuotes of type Stock, Then on Success return a TradeResponse`() = runTest {
        // GIVEN
        val typeAsset = TypeAsset.Stock
        val symbol = "TSLA"
        val quotesDto = quoteAssetDtoFactory.buildList(number = 10)
        mockResponseGetQuotesMarketRemoteDataSource(
            quotesToReturn = quotesDto,
            symbol = symbol,
        )

        // WHEN
        val response = assetsRepository.getQuotes(
            symbol = symbol, typeAsset = typeAsset,
            endDate = LocalDateTime.now(), startDate = LocalDateTime.now().minusDays(1),
        )

        // THEN
        val expectedResponse = quotesDto.map { it.toQuoteAsset() }
        assertThat(response).isInstanceOf(Resource.Success::class)
        (response as Resource.Success).data.quotes.forEachIndexed { i, quoteAsset ->
            assertThat(quoteAsset.askPrice).isEqualTo(expectedResponse[i].askPrice)
        }
    }

    @Test
    fun `When getQuotes of type Stock, Then on Error return a Resource Error`() = runTest {
        // GIVEN
        val typeAsset = TypeAsset.Stock
        val errorMessage = "Error"
        val symbol = "TSLA"
        mockResponseGetQuotesMarketRemoteDataSource(
            isToThrowError = true,
            messageError = errorMessage,
            symbol = symbol,
        )

        // WHEN
        val response = assetsRepository.getQuotes(
            symbol = symbol, typeAsset = typeAsset,
            startDate = LocalDateTime.now().minusDays(1), endDate = LocalDateTime.now(),
        )

        // THEN
        assertThat(response).isInstanceOf(Resource.Error::class)
        assertThat((response as Resource.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `When getRealTimeBars of type Stock, Then on Success return a List of BarAsset`() = runTest {
        // GIVEN
        val typeAsset = TypeAsset.Stock
        val symbol = "TSLA"
        val barsDto = barAssetDtoFactory.buildList(number = 10, symbol = symbol, type = TypeAsset.Stock.value)
        mockResponseGetRealTimeBarsMarketRemoteDataSource(
            barToReturn = barsDto,
        )

        // WHEN
        val response = assetsRepository.getRealTimeBars(
            symbol = symbol, typeAsset = typeAsset,
        ).toList()

        // THEN
        val expectedResponse = barsDto.map { it.toBarAsset() }
        assertThat(response).isNotEmpty()
        assertThat(response.size).isEqualTo(1)
        assertThat(response.first()).isEqualTo(expectedResponse)
    }

    @Test
    fun `When getRealTimeTrades of type Stock, Then on Success return a List of TradeAsset`() = runTest {
        // GIVEN
        val typeAsset = TypeAsset.Stock
        val symbol = "TSLA"
        val tradesDto = tradeAssetDtoFactory.buildList(number = 10, symbol = symbol, type = TypeAsset.Stock.value)
        mockResponseGetRealTimeTradesMarketRemoteDataSource(
            tradesToReturn = tradesDto,
        )

        // WHEN
        val response = assetsRepository.getRealTimeTrades(
            symbol = symbol, typeAsset = typeAsset,
        ).toList()

        // THEN
        val expectedResponse = tradesDto.map { it.toTradeAsset() }
        assertThat(response).isNotEmpty()
        assertThat(response.size).isEqualTo(1)
        response.first().forEachIndexed { i, trade ->
            assertThat(trade.tradePrice).isEqualTo(expectedResponse[i].tradePrice)
        }
    }

    @Test
    fun `When getRealTimeQuotes of type Stock, Then on Success return a List of QuoteAsset`() = runTest {
        // GIVEN
        val typeAsset = TypeAsset.Stock
        val symbol = "TSLA"
        val quotesDto = quoteAssetDtoFactory.buildList(number = 10, symbol = symbol, type = TypeAsset.Stock.value)
        mockResponseGetRealTimeQuotesMarketRemoteDataSource(
            quotesToReturn = quotesDto,
        )

        // WHEN
        val response = assetsRepository.getRealTimeQuotes(
            symbol = symbol, typeAsset = typeAsset,
        ).toList()

        // THEN
        val expectedResponse = quotesDto.map { it.toQuoteAsset() }
        assertThat(response).isNotEmpty()
        assertThat(response.size).isEqualTo(1)
        response.first().forEachIndexed { i, quoteAsset ->
            assertThat(quoteAsset.askPrice).isEqualTo(expectedResponse[i].askPrice)
        }
    }

    @Test
    fun `When statusService of type Stock, Then return a WebSocket Event`() = runTest {
        // GIVEN
        val typeAsset = TypeAsset.Stock
        mockResponseGetStatusServiceMarketRemoteDataSource()

        // WHEN
        val response = assetsRepository.statusService(typeAsset = typeAsset).toList()

        // THEN
        assertThat(response).isNotEmpty()
        response.forEach {
            assertThat(it).isInstanceOf(WebSocket.Event::class.java)
        }
    }

    @Test
    fun `When subscribeUnsubscribeRealTimeFinancialData of type Stock, Then on Success return a SubscriptionMessage`() = runTest {
        // GIVEN
        val typeAsset = TypeAsset.Stock
        val symbol = "TSLA"
        mockResponseSubscribeUnsubscribeRealTimeFinancialDataRemoteDataSource(
            symbol = symbol
        )

        // WHEN
        val response = assetsRepository.subscribeUnsubscribeRealTimeFinancialData(
            action = ActionAlpaca.Subscribe,
            typeAsset = typeAsset,
            symbol = symbol,
        )

        // THEN
        assertThat(response).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun `When subscribeUnsubscribeRealTimeFinancialData of type Stock, Then on Error return a Resource Error`() = runTest {
        // GIVEN
        val typeAsset = TypeAsset.Stock
        val symbol = "AAPL"
        mockResponseSubscribeUnsubscribeRealTimeFinancialDataRemoteDataSource(
            symbol = symbol,
            isToThrowError = true
        )

        // WHEN
        val response = assetsRepository.subscribeUnsubscribeRealTimeFinancialData(
            action = ActionAlpaca.Subscribe,
            typeAsset = typeAsset,
            symbol = symbol,
        )

        // THEN
        assertThat(response).isInstanceOf(Resource.Error::class.java)
    }

    private fun mockResponseGetAssetsAssetsRemoteDataSource(
        assetsToReturn: List<AssetDto> = emptyList(),
        isToThrowError: Boolean = false,
        messageError: String? = null,
        typeAsset: TypeAsset? = null,
    ) {
        if (isToThrowError)
            coEvery {
                assetsRemoteDataSource.getAllAssets(typeAsset = any())
            } throws Exception(messageError ?: "")
        else {
            coEvery {
                assetsRemoteDataSource.getAllAssets(typeAsset = any())
            } returns assetsToReturn

            coEvery {
                assetsLocalDataSource.cacheAssets(data = any(), typeAsset = any())
            } returns assetsToReturn.map { it.toAssetEntity() }

            coEvery {
                assetsLocalDataSource.getAllAssetsOfType(typeAsset = any())
            } returns if (typeAsset != null) assetsToReturn.filter { it.type == typeAsset.value }.map { it.toAssetEntity() }
            else assetsToReturn.map { it.toAssetEntity() }
        }
    }

    private fun mockResponseGetBarsMarketRemoteDataSource(
        barsToReturn: List<BarAssetDto> = emptyList(),
        isToThrowError: Boolean = false,
        messageError: String? = null,
    ) {
        if (isToThrowError)
            coEvery {
                marketRemoteDataSource.getBars(
                    typeAsset = any(), symbol = any(),
                    sort = any(), timeFrame = any(),
                    limit = any(), startDate = any(),
                    endDate = any(),
                )
            } throws Exception(messageError ?: "")
        else
            coEvery {
                marketRemoteDataSource.getBars(
                    typeAsset = any(), symbol = any(),
                    sort = any(), timeFrame = any(),
                    limit = any(), startDate = any(),
                    endDate = any(),
                )
            } returns barsToReturn
    }

    private fun mockResponseGetTradesMarketRemoteDataSource(
        tradesToReturn: List<TradeAssetDto> = emptyList(),
        isToThrowError: Boolean = false,
        messageError: String? = null,
        symbol: String,
    ) {
        if (isToThrowError)
            coEvery {
                marketRemoteDataSource.getTrades(
                    typeAsset = any(), symbol = any(),
                    sort = any(), limit = any(),
                    startDate = any(), endDate = any(),
                )
            } throws Exception(messageError ?: "")
        else
            coEvery {
                marketRemoteDataSource.getTrades(
                    typeAsset = any(), symbol = any(),
                    sort = any(), limit = any(),
                    startDate = any(), endDate = any(),
                )
            } returns TradesResponseDto(
                trades = tradesToReturn,
                symbol = symbol,
                pageToken = null,
            )
    }

    private fun mockResponseGetQuotesMarketRemoteDataSource(
        quotesToReturn: List<QuoteAssetDto> = emptyList(),
        isToThrowError: Boolean = false,
        messageError: String? = null,
        symbol: String,
    ) {
        if (isToThrowError)
            coEvery {
                marketRemoteDataSource.getQuotes(
                    typeAsset = any(), symbol = any(),
                    sort = any(), limit = any(),
                    startDate = any(), endDate = any(),
                )
            } throws Exception(messageError ?: "")
        else
            coEvery {
                marketRemoteDataSource.getQuotes(
                    typeAsset = any(), symbol = any(),
                    sort = any(), limit = any(),
                    startDate = any(), endDate = any(),
                )
            } returns QuotesResponseDto(
                quotes = quotesToReturn,
                symbol = symbol,
                pageToken = null,
            )
    }

    private fun mockResponseGetRealTimeBarsMarketRemoteDataSource(
        barToReturn: List<BarAssetDto> = emptyList(),
    ) {
        coEvery {
            marketRemoteDataSource.getRealTimeBars(
                typeAsset = any(),
            )
        } returns (
            flow {
                emit(listOf(barToReturn.first().copy(symbol="AAPL")))
                emit(barToReturn)
                emit(listOf(barToReturn.first().copy(symbol="AAPL")))
            }
        )
    }

    private fun mockResponseGetRealTimeTradesMarketRemoteDataSource(
        tradesToReturn: List<TradeAssetDto> = emptyList(),
    ) {
        coEvery {
            marketRemoteDataSource.getRealTimeTrades(
                typeAsset = any(),
            )
        } returns (
            flow {
                emit(listOf(tradesToReturn.first().copy(symbol="AAPL")))
                emit(tradesToReturn)
                emit(listOf(tradesToReturn.first().copy(symbol="AAPL")))
            }
        )
    }

    private fun mockResponseGetRealTimeQuotesMarketRemoteDataSource(
        quotesToReturn: List<QuoteAssetDto> = emptyList(),
    ) {
        coEvery {
            marketRemoteDataSource.getRealTimeQuotes(
                typeAsset = any(),
            )
        }.returns(
            flow {
                emit(listOf(quotesToReturn.first().copy(symbol="AAPL")))
                emit(quotesToReturn)
                emit(listOf(quotesToReturn.first().copy(symbol="AAPL")))
            }
        )
    }

    private fun mockResponseGetStatusServiceMarketRemoteDataSource() {
        coEvery {
            marketRemoteDataSource.statusService(
                typeAsset = any(),
            )
        }.returns(
            flow {
                emit(WebSocket.Event.OnConnectionOpened(webSocket = Any()))
                emit(WebSocket.Event.OnMessageReceived(message = Message.Text("HELLO")))
            }
        )
    }

    private fun mockResponseSubscribeUnsubscribeRealTimeFinancialDataRemoteDataSource(
        symbol: String,
        isToThrowError: Boolean = false,
    ) {
        val symbols = listOf(symbol)
        if (isToThrowError)
            coEvery {
                marketRemoteDataSource.subscribeUnsubscribeRealTimeFinancialData(
                    typeAsset = any(), action = any(),
                    symbol = any()
                )
            } throws Exception()
        else
            coEvery {
                marketRemoteDataSource.subscribeUnsubscribeRealTimeFinancialData(
                    typeAsset = any(), action = any(),
                    symbol = any()
                )
            }.returns(
                flow {
                    emit(
                        SubscriptionMessageDto(
                            type = HelperIdentifierMessagesAlpacaWS.Subscription.identifier,
                            quotes = symbols,
                            trades = symbols,
                            bars = symbols,
                        )
                    )
                }
            )
    }
}