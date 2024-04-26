package dev.pinkroom.marketsight.domain.use_case.news

import dev.pinkroom.marketsight.common.Constants.LIMIT_NEWS
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.domain.repository.NewsRepository
import java.time.LocalDateTime
import javax.inject.Inject

class GetNews @Inject constructor(
    private val newsRepository: NewsRepository,
){
    suspend operator fun invoke(
        pageToken: String? = null,
        limitPerPage: Int? = LIMIT_NEWS,
        sortType: SortType? = SortType.DESC,
        symbols: List<String>? = null,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
    ) = newsRepository.getNews(
        pageToken = pageToken,
        limit = limitPerPage,
        sort = sortType,
        symbols = symbols,
        startDate = startDate,
        endDate = endDate,
    )
}