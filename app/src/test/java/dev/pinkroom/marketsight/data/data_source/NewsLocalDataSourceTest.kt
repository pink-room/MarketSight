package dev.pinkroom.marketsight.data.data_source

import androidx.sqlite.db.SimpleSQLiteQuery
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pinkroom.marketsight.common.Constants
import dev.pinkroom.marketsight.common.formatToStandardIso
import dev.pinkroom.marketsight.data.local.DbTransaction
import dev.pinkroom.marketsight.data.local.dao.ImagesDao
import dev.pinkroom.marketsight.data.local.dao.NewsDao
import dev.pinkroom.marketsight.data.local.entity.NewsEntity
import dev.pinkroom.marketsight.data.local.entity.NewsImagesCrossRefEntity
import dev.pinkroom.marketsight.data.mapper.toImageEntity
import dev.pinkroom.marketsight.data.mapper.toNewsEntity
import dev.pinkroom.marketsight.data.mapper.toNewsMap
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.NewsDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.NewsResponseDto
import dev.pinkroom.marketsight.factories.NewsDtoFactory
import dev.pinkroom.marketsight.util.MainCoroutineRule
import dev.pinkroom.marketsight.util.mockAndExecuteTransaction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime


@OptIn(ExperimentalCoroutinesApi::class)
class NewsLocalDataSourceTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val newsDtoFactory = NewsDtoFactory()
    private val imagesDao = mockk<ImagesDao>(relaxUnitFun = true, relaxed = true)
    private val newsDao = mockk<NewsDao>(relaxUnitFun = true, relaxed = true)
    private val dbTransaction = mockk<DbTransaction>()
    private val alpacaLocalDataSource = NewsLocalDataSource(
        newsDao = newsDao,
        imagesDao = imagesDao,
        dbTransaction = dbTransaction,
    )

    @Before
    fun setup() = runTest {
        dbTransaction.mockAndExecuteTransaction()
    }

    @Test
    fun `When cacheNews and clear cache, Then delete all cache, save and return the new cached news`() = runTest {
        // GIVEN
        val newsDto = newsDtoFactory.buildList(number = 20)
        val newsMap = NewsResponseDto(news = newsDto).toNewsMap()
        mockGetNewsDao(data = newsMap.keys.toList())

        // WHEN
        val result = alpacaLocalDataSource.cacheNews(
            data = newsMap,
            limit = 20,
            clearCurrentCache = true,
            offset = 0,
            isAsc = false,
            startDate = null,
            endDate = null,
            symbols = null,
        )

        // THEN
        newsMap.forEach { news ->
            coVerify { imagesDao.insert(images = news.value) }
            val newsImagesRef = news.value.map {
                NewsImagesCrossRefEntity(
                    newsId = news.key.id,
                    imageId = it.url
                )
            }
            coVerify { newsDao.insertNewsImagesCrossRef(ref = newsImagesRef) }
        }
        coVerify { newsDao.clearAll() }
        coVerify { imagesDao.clearAll() }
        coVerify { newsDao.clearAllCrossRefNewsImages() }
        coVerify { newsDao.insert(news = newsMap.keys.toList()) }
        assertThat(result).isEqualTo(newsDto.map { it.toNewsEntity() })
    }

    @Test
    fun `When cacheNews and don't clear cache, Then save and return the new cached news`() = runTest {
        // GIVEN
        val newsDto = newsDtoFactory.buildList(number = 20)
        val newsMap = NewsResponseDto(news = newsDto).toNewsMap()
        mockGetNewsDao(data = newsMap.keys.toList())

        // WHEN
        val result = alpacaLocalDataSource.cacheNews(
            data = newsMap,
            limit = 20,
            clearCurrentCache = false,
            offset = 0,
            isAsc = false,
            startDate = null,
            endDate = null,
            symbols = null,
        )

        // THEN
        newsMap.forEach { news ->
            coVerify { imagesDao.insert(images = news.value) }
            val newsImagesRef = news.value.map {
                NewsImagesCrossRefEntity(
                    newsId = news.key.id,
                    imageId = it.url
                )
            }
            coVerify { newsDao.insertNewsImagesCrossRef(ref = newsImagesRef) }
        }
        coVerify(exactly = 0) { newsDao.clearAll() }
        coVerify(exactly = 0) { imagesDao.clearAll() }
        coVerify(exactly = 0) { newsDao.clearAllCrossRefNewsImages() }
        coVerify { newsDao.insert(news = newsMap.keys.toList()) }
        assertThat(result).isEqualTo(newsDto.map { it.toNewsEntity() })
    }

    @Test
    fun `When getImagesRelatedToNews, Then return images related to news`() = runTest {
        // GIVEN
        val newsDto = newsDtoFactory.buildList(number = 20)
        val newsToGetImages = newsDto.random()
        mockGetImagesRelatedToNewsImagesDao(data = newsDto, id = newsToGetImages.id)

        // WHEN
        val result = alpacaLocalDataSource.getImagesRelatedToNews(
            newsId = newsToGetImages.id
        )

        // THEN
        coVerify { imagesDao.getImagesRelatedToNews(newsId = newsToGetImages.id) }
        assertThat(result).isEqualTo(newsToGetImages.images.map { it.toImageEntity() })
    }

    @Test
    fun `When getNews, Then verify if query it is well built`() = runTest {
        // GIVEN
        val queryCaptured = slot<SimpleSQLiteQuery>()
        coEvery { newsDao.getNews(query = capture(queryCaptured)) } coAnswers {
            emptyList()
        }

        // WHEN
        val limit = 20
        val offset = 0
        alpacaLocalDataSource.getNews(
            symbols = null,
            isAsc = true,
            endDate = null,
            startDate = null,
            offset = offset,
            limit = limit,
        )

        // THEN
        var expectedQueryArg = "SELECT * FROM ${Constants.NEWS_ENTITY_NAME} WHERE"
        expectedQueryArg += " (${Constants.UPDATE_AT_COLUMN_NAME_NEWS_ENTITY} BETWEEN '' AND '${LocalDateTime.now().plusDays(1).formatToStandardIso()}')"
        expectedQueryArg += " ORDER BY ${Constants.UPDATE_AT_COLUMN_NAME_NEWS_ENTITY} ASC"
        expectedQueryArg += " LIMIT $limit OFFSET $offset"
        coVerify { newsDao.getNews(query = any()) }
        assertThat(queryCaptured.captured.sql).isEqualTo(expectedQueryArg)
    }

    @Test
    fun `When getNews with date and symbols, Then verify if query it is well built`() = runTest {
        // GIVEN
        val queryCaptured = slot<SimpleSQLiteQuery>()
        coEvery { newsDao.getNews(query = capture(queryCaptured)) } coAnswers {
            emptyList()
        }

        // WHEN
        val limit = 40
        val offset = 10
        val symbols = listOf("TSLA","AAPL")
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now().minusDays(1)
        alpacaLocalDataSource.getNews(
            symbols = symbols,
            isAsc = false,
            endDate = endDate.formatToStandardIso(),
            startDate = startDate.formatToStandardIso(),
            offset = offset,
            limit = limit,
        )

        // THEN
        var expectedQueryArg = "SELECT * FROM ${Constants.NEWS_ENTITY_NAME} WHERE"
        expectedQueryArg += " (${Constants.UPDATE_AT_COLUMN_NAME_NEWS_ENTITY} BETWEEN '${startDate.formatToStandardIso()}' AND '${endDate.formatToStandardIso()}')"
        expectedQueryArg += symbols.joinToString(prefix = " AND (", separator = " OR ", postfix = ")") {
            "${Constants.SYMBOL_COLUMN_NAME_NEWS_ENTITY} LIKE '%$it%'"
        }
        expectedQueryArg += " ORDER BY ${Constants.UPDATE_AT_COLUMN_NAME_NEWS_ENTITY} DESC"
        expectedQueryArg += " LIMIT $limit OFFSET $offset"
        coVerify { newsDao.getNews(query = any()) }
        assertThat(queryCaptured.captured.sql).isEqualTo(expectedQueryArg)
    }

    private fun mockGetNewsDao(
        data: List<NewsEntity>,
    ){
        coEvery {
            newsDao.getNews(query = any())
        }.returns(
            data
        )
    }

    private fun mockGetImagesRelatedToNewsImagesDao(
        data: List<NewsDto>,
        id: Long,
    ){
        coEvery {
            imagesDao.getImagesRelatedToNews(newsId = id)
        }.returns(
            data.find { it.id == id }?.images?.map { it.toImageEntity() } ?: emptyList()
        )
    }
}