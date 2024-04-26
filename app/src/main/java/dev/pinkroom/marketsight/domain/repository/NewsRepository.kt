package dev.pinkroom.marketsight.domain.repository

import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.Constants.LIMIT_NEWS
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.domain.model.news.NewsInfo
import dev.pinkroom.marketsight.domain.model.news.NewsResponse
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface NewsRepository {
    fun getRealTimeNews(): Flow<List<NewsInfo>>
    suspend fun changeFilterNews(
        symbols: List<String>,
        actionAlpaca: ActionAlpaca,
    ): Resource<List<String>>

    suspend fun getNews(
        symbols: List<String>? = null,
        limit: Int? = LIMIT_NEWS,
        pageToken: String? = null,
        sort: SortType? = SortType.DESC,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
    ): Resource<NewsResponse>
}