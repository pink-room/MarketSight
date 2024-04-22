package dev.pinkroom.marketsight.domain.use_case.news

import dev.pinkroom.marketsight.common.Constants.LIMIT_NEWS
import dev.pinkroom.marketsight.domain.repository.NewsRepository
import javax.inject.Inject

class GetNews @Inject constructor(
    private val newsRepository: NewsRepository,
){
    suspend operator fun invoke(
        pageToken: String? = null,
        limitPerPage: Int? = LIMIT_NEWS,
    ) = newsRepository.getNews(
        pageToken = pageToken,
        limit = limitPerPage,
    )
}