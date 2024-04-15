package dev.pinkroom.marketsight.data.data_source

import assertk.assertThat
import assertk.assertions.any
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.github.javafaker.Faker
import com.google.gson.Gson
import com.tinder.scarlet.WebSocket
import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.data.remote.AlpacaNewsApi
import dev.pinkroom.marketsight.data.remote.AlpacaNewsService
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.NewsResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.ErrorMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.NewsMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.SubscriptionMessageDto
import dev.pinkroom.marketsight.data.remote.model.request.MessageAlpacaService
import dev.pinkroom.marketsight.factories.NewsDtoFactory
import dev.pinkroom.marketsight.factories.NewsFactory
import dev.pinkroom.marketsight.util.MainCoroutineRule
import dev.pinkroom.marketsight.util.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NewsRemoteDataSourceTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val gson = Gson()
    private val faker = Faker()
    private val newsFactory = NewsFactory()
    private val newsDtoFactory = NewsDtoFactory()
    private val alpacaNewsService = mockk<AlpacaNewsService>(relaxed = true, relaxUnitFun = true)
    private val alpacaNewsApi = mockk<AlpacaNewsApi>()
    private val dispatchers = TestDispatcherProvider()
    private val alpacaRemoteDataSource = NewsRemoteDataSource(
        gson = gson,
        alpacaNewsService = alpacaNewsService,
        alpacaNewsApi = alpacaNewsApi,
        dispatchers = dispatchers,
    )

    @Test
    fun `Given params, when init subscribe news with all topics, then sendMessage is called with correct params`() = runTest {
        // GIVEN
        mockStartAlpacaServiceWithSuccess()
        val symbols = listOf("*")

        // WHEN
        alpacaRemoteDataSource.subscribeNews(symbols = symbols).firstOrNull()

        // THEN
        val expectedMessageArgs = MessageAlpacaService(action = ActionAlpaca.Subscribe.action, news = symbols)
        verify { alpacaNewsService.sendMessage(message = expectedMessageArgs) }
    }

    @Test
    fun `Given params, when send subscribe message to service, then sendMessage is called with correct params`() = runTest {
        // GIVEN
        val messageToSend = MessageAlpacaService(action = ActionAlpaca.Subscribe.action, news = listOf("TSLA","AAPL"), quotes = listOf("TSLA"))
        mockMessageSubscriptionServiceWithSuccess(messageToSend)

        // WHEN
        val response = alpacaRemoteDataSource.sendSubscribeMessageToAlpacaService(message = messageToSend).first()

        // THEN
        verify { alpacaNewsService.sendMessage(message = messageToSend) }
        assertThat(response is Resource.Success).isTrue()
        val data = response as Resource.Success
        assertThat(data.data.news).isEqualTo(messageToSend.news)
        assertThat(data.data.quotes).isEqualTo(messageToSend.quotes)
        assertThat(data.data.trades).isEqualTo(messageToSend.trades)
    }

    @Test
    fun `When receive message on getRealTimeNews, then try to parse Json to NewsInfoDto class`() = runTest {
        // GIVEN
        val listNews = newsFactory.buildList()
        mockNewsServiceWithSuccess(newsList = listNews)

        // WHEN
        val response = alpacaRemoteDataSource.getRealTimeNews().toList()

        // THEN
        verify { alpacaNewsService.observeResponse() }
        val allIdsSent = listNews.map { it.id }
        assertThat(response).isNotEmpty()
        response.forEach { newsReturned ->
            assertThat(newsReturned).isNotEmpty()
            newsReturned.forEach { news ->
                assertThat(allIdsSent).any { it.isEqualTo(news.id)}
            }
        }
    }

    @Test
    fun `Given params, then alpaca api call getNews is called with correct params`() = runTest {
        // GIVEN
        val symbols = listOf("*")
        val limit = 15
        val pageToken = null
        val sort = SortType.DESC
        mockNewsResponseApiWithSuccess(limit)

        // WHEN
        val response = alpacaRemoteDataSource.getNews(
            symbols = symbols,
            limit = limit,
            pageToken = pageToken,
            sort = sort
        )

        // THEN
        coVerify {
            alpacaNewsApi.getNews(
                symbols = symbols.joinToString(","),
                perPage = limit,
                pageToken = pageToken,
                sort = sort.type,
            )
        }
        assertThat(response.news.size).isEqualTo(limit)
    }

    @Test
    fun `When receive message from WS, then message is not related with news on getRealTimeNews`() = runTest {
        // GIVEN
        mockNewsServiceWithMessageNotExpected()

        // WHEN
        val response = alpacaRemoteDataSource.getRealTimeNews().firstOrNull()

        // THEN
        verify { alpacaNewsService.observeResponse() }
        assertThat(response).isNull()
    }

    @Test
    fun `When receive message from WS, then alpaca service receive error message on sendSubscribeMessageToAlpacaService`() = runTest {
        // GIVEN
        val messageToSend = MessageAlpacaService(action = ActionAlpaca.Subscribe.action, news = listOf("TSLA","AAPL"), quotes = listOf("TSLA"))
        mockMessageSubscriptionServiceWithError()

        // WHEN
        val response = alpacaRemoteDataSource.sendSubscribeMessageToAlpacaService(messageToSend).first()

        // THEN
        verify { alpacaNewsService.observeResponse() }
        assertThat(response is Resource.Error).isTrue()
    }

    private fun mockNewsResponseApiWithSuccess(limit: Int) {
        coEvery { alpacaNewsApi.getNews(
            symbols = any(),
            perPage = any(),
            pageToken = any(),
            sort = any(),
        ) }.returns(
            NewsResponseDto(
                news = newsDtoFactory.buildList(number = limit),
                nextPageToken = faker.lorem().word()
            )
        )
    }

    private fun mockNewsServiceWithSuccess(newsList: List<NewsMessageDto>) {
        every { alpacaNewsService.observeResponse() }.returns(
            flow {
                emit(listOf(gson.toJsonTree(newsList.first())))
                delay(3000) // Simulate WS API
                val listNewsJson = newsList.map {
                    gson.toJsonTree(it)
                }
                emit(listNewsJson)
            }
        )
    }

    private fun mockNewsServiceWithMessageNotExpected() {
        every { alpacaNewsService.observeResponse() }.returns(
            flow {
                emit(listOf(gson.toJsonTree(
                    ErrorMessageDto(
                        type = "error",
                        code = 403,
                        msg = "Not authenticated"
                    )
                )))
            }
        )
    }

    private fun mockStartAlpacaServiceWithSuccess() {
        every { alpacaNewsService.sendMessage(any()) }.returns(Unit)

        every { alpacaNewsService.observeOnConnectionEvent() }.returns(
            flow {
                emit(WebSocket.Event.OnConnectionOpened(webSocket = Any()))
            }
        )
    }

    private fun mockMessageSubscriptionServiceWithSuccess(messageAlpacaService: MessageAlpacaService) {
        val returnedMessageService = SubscriptionMessageDto(
            type = "subscription",
            news = messageAlpacaService.news,
            quotes = messageAlpacaService.quotes,
            trades = messageAlpacaService.trades,
        )

        every { alpacaNewsService.sendMessage(any()) } returns(Unit)
        every { alpacaNewsService.observeResponse() }.returns(
            flow {
                emit(listOf(gson.toJsonTree(returnedMessageService)))
            }
        )
    }

    private fun mockMessageSubscriptionServiceWithError() {
        val errorMessage = ErrorMessageDto(
            type = "error",
            msg = "Not Found",
            code = 404,
        )
        every { alpacaNewsService.sendMessage(any()) } returns(Unit)
        every { alpacaNewsService.observeResponse() }.returns(
            flow {
                emit(listOf(gson.toJsonTree(errorMessage)))
            }
        )
    }
}