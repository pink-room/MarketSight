package dev.pinkroom.marketsight.domain.use_case.news

import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.domain.repository.NewsRepository
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetRealTimeNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
    private val dispatchers: DispatcherProvider,
){
    operator fun invoke() = newsRepository.getRealTimeNews().flowOn(dispatchers.IO)
}