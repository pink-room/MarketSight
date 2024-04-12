package dev.pinkroom.marketsight.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import com.github.javafaker.Faker
import com.tinder.scarlet.WebSocket
import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.data.data_source.NewsRemoteDataSource
import dev.pinkroom.marketsight.data.mapper.toNewsInfo
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.NewsResponseDto
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NewsRepositoryTest{

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val faker = Faker()
    private val newsFactory = NewsFactory()
    private val newsDtoFactory = NewsDtoFactory()
    private val newsRemoteDataSource = mockk<NewsRemoteDataSource>(relaxed = true, relaxUnitFun = true)
    private val dispatchers = TestDispatcherProvider()
    private val newsRepository = NewsRepositoryImp(
        newsRemoteDataSource = newsRemoteDataSource,
        dispatchers = dispatchers,
    )

    @Test
    fun `Given params, when init subscribe news, then subscribeNews is called with correct params and emit values`() = runTest {
        // GIVEN
        mockSubscribeNewsWithSuccess()

        // WHEN
        val response = newsRepository.subscribeNews().first()

        // THEN
        assertThat(response is Resource.Success).isTrue()
    }

    @Test
    fun `When receive message on getRealTimeNews, then emit NewsInfo class`() = runTest {
        // GIVEN
        val listNews = newsFactory.buildList()
        mockNewsServiceWithSuccess(newsList = listNews)

        // WHEN
        val response = newsRepository.getRealTimeNews().first()

        // THEN
        verify { newsRemoteDataSource.getRealTimeNews() }
        val expectedResult = listNews.map { it.toNewsInfo() }
        assertThat(response).isNotEmpty()
        assertThat(response).isEqualTo(expectedResult)
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
        val response = newsRepository.getNews(
            symbols = symbols,
            limit = limit,
            pageToken = pageToken,
            sort = sort
        )

        // THEN
        coVerify {
            newsRemoteDataSource.getNews(
                symbols = symbols,
                limit = limit,
                pageToken = pageToken,
                sort = sort,
            )
        }
        assertThat(response is Resource.Success).isTrue()
        val data = response as Resource.Success
        assertThat(data.data.news.size).isEqualTo(limit)
    }

    @Test
    fun `Given params, then alpaca api throw error on call getNews`() = runTest {
        // GIVEN
        mockNewsResponseApiWithError()

        // WHEN
        val response = newsRepository.getNews(
            symbols = null,
            limit = null,
            pageToken = null,
            sort = null
        )

        // THEN
        coVerify {
            newsRemoteDataSource.getNews(
                symbols = any(),
                pageToken = any(),
                sort = any(),
                limit = any(),
            )
        }
        assertThat(response is Resource.Error).isTrue()
    }

    @Test
    fun `Given params, when change filters, then receive list with symbols subscribed`() = runTest {
        // GIVEN
        val messageToSend = MessageAlpacaService(action = ActionAlpaca.Subscribe.action, news = listOf("TSLA","AAPL"))
        mockMessageSubscriptionServiceWithSuccess(messageToSend)

        // WHEN
        val response = newsRepository.changeFilterNews(symbols = messageToSend.news!!, actionAlpaca = ActionAlpaca.Subscribe)

        // THEN
        verify { newsRemoteDataSource.sendSubscribeMessageToAlpacaService(message = messageToSend) }
        assertThat(response is Resource.Success).isTrue()
        val data = response as Resource.Success
        assertThat(data.data).isEqualTo(messageToSend.news)
    }

    @Test
    fun `Given params, when change filters, then error occur and return list with symbols that fail to subscribe`() = runTest {
        // GIVEN
        val messageToSend = MessageAlpacaService(action = ActionAlpaca.Subscribe.action, news = listOf("TSLA","AAPL"))
        mockMessageSubscriptionServiceWithError()

        // WHEN
        val response = newsRepository.changeFilterNews(symbols = messageToSend.news!!, actionAlpaca = ActionAlpaca.Subscribe)

        // THEN
        verify { newsRemoteDataSource.sendSubscribeMessageToAlpacaService(message = messageToSend) }
        assertThat(response is Resource.Error).isTrue()
        val data = response as Resource.Error
        assertThat(data.data).isEqualTo(messageToSend.news)
    }


    private fun mockNewsResponseApiWithSuccess(limit: Int) {
        coEvery {
            newsRemoteDataSource.getNews(
                symbols = any(),
                limit = any(),
                pageToken = any(),
                sort = any(),
            )
        }.returns(
            NewsResponseDto(
                news = newsDtoFactory.buildList(number = limit),
                nextPageToken = faker.lorem().word()
            )
        )
    }

    private fun mockNewsResponseApiWithError() {
        coEvery {
            newsRemoteDataSource.getNews(
                symbols = any(),
                limit = any(),
                pageToken = any(),
                sort = any(),
            )
        }.throws(
            Exception("Test Error")
        )
    }

    private fun mockNewsServiceWithSuccess(newsList: List<NewsMessageDto>) {
        every { newsRemoteDataSource.getRealTimeNews() }.returns(
            flow {
                emit(newsList)
            }
        )
    }

    private fun mockSubscribeNewsWithSuccess(){
        every { newsRemoteDataSource.subscribeNews(any()) }.returns(
            flow {
                emit(Resource.Success(data = WebSocket.Event.OnConnectionOpened(webSocket = Any())))
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

        every { newsRemoteDataSource.sendSubscribeMessageToAlpacaService(any()) } returns(
            flow {
                emit(Resource.Success(data = returnedMessageService))
            }
        )
    }

    private fun mockMessageSubscriptionServiceWithError() {
        every { newsRemoteDataSource.sendSubscribeMessageToAlpacaService(any()) } returns(
            flow {
                emit(Resource.Error())
            }
        )
    }
}