package dev.pinkroom.marketsight.data.repository

import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.data.data_source.NewsRemoteDataSource
import dev.pinkroom.marketsight.data.mapper.toNewsInfo
import dev.pinkroom.marketsight.data.mapper.toNewsResponse
import dev.pinkroom.marketsight.data.remote.model.request.MessageAlpacaService
import dev.pinkroom.marketsight.domain.model.news.NewsResponse
import dev.pinkroom.marketsight.domain.repository.NewsRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.last
import java.time.LocalDateTime
import javax.inject.Inject

class NewsRepositoryImp @Inject constructor(
    private val newsRemoteDataSource: NewsRemoteDataSource,
    private val dispatchers: DispatcherProvider,
): NewsRepository {

    override fun getRealTimeNews() = flow {
        newsRemoteDataSource.getRealTimeNews().collect{
            emit(it.map { item -> item.toNewsInfo() })
        }
    }.flowOn(dispatchers.IO)

    override suspend fun changeFilterRealTimeNews(
        symbols: List<String>,
        actionAlpaca: ActionAlpaca,
    ): Resource<List<String>> = flow {
        val message = MessageAlpacaService(action = actionAlpaca.action, news = symbols)
        newsRemoteDataSource.sendSubscribeMessageToAlpacaService(message = message).collect{ response ->
            when(response){
                is Resource.Error -> {
                    emit(Resource.Error(data = symbols))
                }
                is Resource.Success -> emit(Resource.Success(data = response.data.news ?: symbols))
            }
        }
    }.flowOn(dispatchers.IO).last()

    override suspend fun getNews(
        symbols: List<String>?,
        limit: Int?,
        pageToken: String?,
        sort: SortType?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
    ): Resource<NewsResponse> {
        return try {
            val response = newsRemoteDataSource.getNews(
                symbols = symbols,
                limit = limit,
                pageToken = pageToken,
                sort = sort,
                startDate = startDate,
                endDate = endDate,
            )
            Resource.Success(data = response.toNewsResponse())
        } catch (e: Exception){
            e.printStackTrace()
            Resource.Error(message = e.message ?: "Something Went Wrong")
        }
    }
}