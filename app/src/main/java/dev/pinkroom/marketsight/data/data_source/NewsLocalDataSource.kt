package dev.pinkroom.marketsight.data.data_source

import androidx.sqlite.db.SimpleSQLiteQuery
import dev.pinkroom.marketsight.common.Constants.NEWS_ENTITY_NAME
import dev.pinkroom.marketsight.common.Constants.SYMBOL_COLUMN_NAME_NEWS_ENTITY
import dev.pinkroom.marketsight.common.Constants.UPDATE_AT_COLUMN_NAME_NEWS_ENTITY
import dev.pinkroom.marketsight.common.formatToStandardIso
import dev.pinkroom.marketsight.data.local.DbTransaction
import dev.pinkroom.marketsight.data.local.dao.ImagesDao
import dev.pinkroom.marketsight.data.local.dao.NewsDao
import dev.pinkroom.marketsight.data.local.entity.ImagesEntity
import dev.pinkroom.marketsight.data.local.entity.NewsEntity
import dev.pinkroom.marketsight.data.local.entity.NewsImagesCrossRefEntity
import java.time.LocalDateTime
import javax.inject.Inject

class NewsLocalDataSource @Inject constructor(
    private val dbTransaction: DbTransaction,
    private val newsDao: NewsDao,
    private val imagesDao: ImagesDao,
) {
    suspend fun cacheNews(
        data: Map<NewsEntity, List<ImagesEntity>>,
        clearCurrentCache: Boolean = false,
        symbols: List<String>?,
        isAsc: Boolean,
        limit: Int,
        offset: Int,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
    ) = dbTransaction {
        if (clearCurrentCache) {
            deleteAllNews()
            deleteAllImages()
            deleteAllCrossRefNewsImages()
        }
        newsDao.insert(news = data.keys.toList())
        data.forEach { news ->
            imagesDao.insert(images = news.value)
            val newsImagesRef = news.value.map {
                NewsImagesCrossRefEntity(
                    newsId = news.key.id,
                    imageId = it.url
                )
            }
            newsDao.insertNewsImagesCrossRef(ref = newsImagesRef)
        }
        getNews(
            symbols = symbols,
            isAsc = isAsc,
            limit = limit,
            offset = offset,
            startDate = startDate?.formatToStandardIso(),
            endDate = endDate?.formatToStandardIso(),
        )
    }

    private suspend fun deleteAllNews() = newsDao.clearAll()

    private suspend fun deleteAllImages() = imagesDao.clearAll()

    private suspend fun deleteAllCrossRefNewsImages() = newsDao.clearAllCrossRefNewsImages()

    suspend fun getNews(
        symbols: List<String>?,
        isAsc: Boolean,
        limit: Int,
        offset: Int,
        startDate: String?,
        endDate: String?,
    ): List<NewsEntity> {
        val start = startDate ?: ""
        val end = endDate ?: LocalDateTime.now().plusDays(1).formatToStandardIso()
        val orderBy = if (isAsc) "ASC" else "DESC"
        var query = "SELECT * FROM $NEWS_ENTITY_NAME WHERE"
        query += " ($UPDATE_AT_COLUMN_NAME_NEWS_ENTITY BETWEEN '$start' AND '$end')"
        query += symbols?.let {
            symbols.joinToString(prefix = " AND (", separator = " OR ", postfix = ")") {
                "$SYMBOL_COLUMN_NAME_NEWS_ENTITY LIKE '%$it%'"
            }
        } ?: ""
        query += " ORDER BY $UPDATE_AT_COLUMN_NAME_NEWS_ENTITY $orderBy"
        query += " LIMIT $limit OFFSET $offset"

        return newsDao.getNews(SimpleSQLiteQuery(query))
    }

    suspend fun getImagesRelatedToNews(newsId: Long) = imagesDao.getImagesRelatedToNews(newsId = newsId)
}