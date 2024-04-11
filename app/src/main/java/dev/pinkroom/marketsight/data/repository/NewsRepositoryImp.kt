package dev.pinkroom.marketsight.data.repository

import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.data.data_source.AlpacaRemoteDataSource
import dev.pinkroom.marketsight.data.remote.model.request.MessageAlpacaService
import dev.pinkroom.marketsight.domain.model.news.NewsResponse
import dev.pinkroom.marketsight.domain.repository.NewsRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single
import javax.inject.Inject

class NewsRepositoryImp @Inject constructor(
    private val remoteDataSource: AlpacaRemoteDataSource,
    private val dispatchers: DispatcherProvider,
): NewsRepository {
    override fun subscribeNews(symbols: List<String>) = flow {
        remoteDataSource.subscribeNews(symbols = symbols).collect{
            emit(it)
        }
    }.flowOn(dispatchers.IO)

    override fun getRealTimeNews() = flow {
        remoteDataSource.getRealTimeNews().collect{
            emit(it)
        }
    }.flowOn(dispatchers.IO)

    override suspend fun changeFilterNews(
        symbols: List<String>,
        actionAlpaca: ActionAlpaca,
    ): Resource<List<String>> = flow {
        val message = MessageAlpacaService(action = actionAlpaca.action, news = symbols)
        remoteDataSource.sendMessageToAlpacaService(message = message).collect{ response ->
            when(response){
                is Resource.Error -> emit(Resource.Error(data = symbols))
                is Resource.Success -> emit(Resource.Success(data = response.data.news ?: symbols))
                else -> Unit
            }
        }
    }.flowOn(dispatchers.IO).single()

    override suspend fun getNews(
        symbols: List<String>?,
        limit: Int?,
        pageToken: String?,
        sort: SortType?
    ): Resource<NewsResponse> {
        return remoteDataSource.getNews(
            symbols = symbols,
            limit = limit,
            pageToken = pageToken,
            sort = sort,
        )
    }
}