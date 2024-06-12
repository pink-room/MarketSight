package dev.pinkroom.marketsight.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import com.github.javafaker.Faker
import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.data.data_source.NewsLocalDataSource
import dev.pinkroom.marketsight.data.data_source.NewsRemoteDataSource
import dev.pinkroom.marketsight.data.mapper.toImageEntity
import dev.pinkroom.marketsight.data.mapper.toNewsEntity
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
class NewsRepositoryImpTest{

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val faker = Faker()
    private val newsFactory = NewsFactory()
    private val newsDtoFactory = NewsDtoFactory()
    private val newsRemoteDataSource = mockk<NewsRemoteDataSource>(relaxed = true, relaxUnitFun = true)
    private val newsLocalDataSource = mockk<NewsLocalDataSource>()
    private val dispatchers = TestDispatcherProvider()
    private val newsRepository = NewsRepositoryImp(
        newsRemoteDataSource = newsRemoteDataSource,
        newsLocalDataSource = newsLocalDataSource,
        dispatchers = dispatchers,
    )

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
            sort = sort,
            fetchFromRemote = true,
            offset = 0,
            cleanCache = true,
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
        mockNewsResponseApiWithError(limit = 0, isCacheEmpty = true)

        // WHEN
        val response = newsRepository.getNews(
            symbols = null,
            limit = null,
            pageToken = null,
            sort = null,
            fetchFromRemote = true,
            offset = 0,
            cleanCache = true,
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
        coVerify {
            newsLocalDataSource.getNews(
                symbols = any(),
                isAsc = any(),
                limit = any(),
                offset = any(),
                endDate = any(),
                startDate = any(),
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
        val response = newsRepository.changeFilterRealTimeNews(symbols = messageToSend.news!!, actionAlpaca = ActionAlpaca.Subscribe)

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
        val response = newsRepository.changeFilterRealTimeNews(symbols = messageToSend.news!!, actionAlpaca = ActionAlpaca.Subscribe)

        // THEN
        verify { newsRemoteDataSource.sendSubscribeMessageToAlpacaService(message = messageToSend) }
        assertThat(response is Resource.Error).isTrue()
        val data = response as Resource.Error
        assertThat(data.data).isEqualTo(messageToSend.news)
    }


    private fun mockNewsResponseApiWithSuccess(limit: Int) {
        val news = newsDtoFactory.buildList(number = limit)
        coEvery {
            newsRemoteDataSource.getNews(
                symbols = any(),
                limit = any(),
                pageToken = any(),
                sort = any(),
            )
        }.returns(
            NewsResponseDto(
                news = news,
                nextPageToken = faker.lorem().word()
            )
        )

        coEvery {
            newsLocalDataSource.cacheNews(
                symbols = any(),
                limit = any(),
                offset = any(),
                endDate = any(),
                startDate = any(),
                isAsc = any(),
                data = any(),
                clearCurrentCache = any()
            )
        }.returns(
            news.map { it.toNewsEntity() }
        )

        coEvery {
            newsLocalDataSource.getImagesRelatedToNews(
                newsId = any()
            )
        }.returns(
            news.firstOrNull()?.images?.map { it.toImageEntity() } ?: emptyList()
        )
    }

    private fun mockNewsResponseApiWithError(limit: Int, isCacheEmpty: Boolean) {
        coEvery {
            newsRemoteDataSource.getNews(
                symbols = any(),
                limit = any(),
                pageToken = any(),
                sort = any(),
                startDate = any(),
                endDate = any(),
            )
        }.throws(
            Exception("Test Error")
        )

        val news = if (isCacheEmpty) emptyList()
        else newsDtoFactory.buildList(number = limit)
        coEvery {
            newsLocalDataSource.cacheNews(
                symbols = any(),
                limit = any(),
                offset = any(),
                endDate = any(),
                startDate = any(),
                isAsc = any(),
                data = any(),
                clearCurrentCache = any()
            )
        }.returns(
            news.map { it.toNewsEntity() }
        )

        coEvery {
            newsLocalDataSource.getImagesRelatedToNews(
                newsId = any()
            )
        }.returns(
            news.firstOrNull()?.images?.map { it.toImageEntity() } ?: emptyList()
        )

        coEvery {
            newsLocalDataSource.getNews(
                symbols = any(),
                isAsc = any(),
                limit = any(),
                offset = any(),
                endDate = any(),
                startDate = any(),
            )
        }.returns(
            news.map { it.toNewsEntity() }
        )
    }

    private fun mockNewsServiceWithSuccess(newsList: List<NewsMessageDto>) {
        every { newsRemoteDataSource.getRealTimeNews() }.returns(
            flow {
                emit(newsList)
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