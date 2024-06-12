package dev.pinkroom.marketsight.data.repository

import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.Constants.DEFAULT_PAGINATION_TOKEN_REQUEST_NEWS
import dev.pinkroom.marketsight.common.Constants.LIMIT_NEWS
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.common.formatToStandardIso
import dev.pinkroom.marketsight.data.data_source.NewsLocalDataSource
import dev.pinkroom.marketsight.data.data_source.NewsRemoteDataSource
import dev.pinkroom.marketsight.data.mapper.toNewsInfo
import dev.pinkroom.marketsight.data.mapper.toNewsMap
import dev.pinkroom.marketsight.data.remote.model.request.MessageAlpacaService
import dev.pinkroom.marketsight.domain.model.news.NewsResponse
import dev.pinkroom.marketsight.domain.repository.NewsRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.last
import java.sql.SQLException
import java.time.LocalDateTime
import javax.inject.Inject

class NewsRepositoryImp @Inject constructor(
    private val newsRemoteDataSource: NewsRemoteDataSource,
    private val newsLocalDataSource: NewsLocalDataSource,
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
        cleanCache: Boolean,
        fetchFromRemote: Boolean,
        symbols: List<String>?,
        limit: Int?,
        offset: Int,
        pageToken: String?,
        sort: SortType?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
    ): Resource<NewsResponse> {
        return try {
            var nextPageToken: String?
            val news = if (fetchFromRemote) {
                val responseRequest = newsRemoteDataSource.getNews(
                    symbols = symbols,
                    limit = limit,
                    pageToken = pageToken,
                    sort = sort,
                    startDate = startDate,
                    endDate = endDate,
                )
                nextPageToken = responseRequest.nextPageToken
                newsLocalDataSource.cacheNews(
                    data = responseRequest.toNewsMap(),
                    clearCurrentCache = cleanCache,
                    symbols = symbols,
                    limit = limit ?: LIMIT_NEWS,
                    offset = offset,
                    startDate = startDate,
                    endDate = endDate,
                    isAsc = sort?.let { it is SortType.ASC } ?: false,
                )
            } else {
                nextPageToken = DEFAULT_PAGINATION_TOKEN_REQUEST_NEWS
                newsLocalDataSource.getNews(
                    symbols = symbols,
                    limit = limit ?: LIMIT_NEWS,
                    offset = offset,
                    startDate = startDate?.formatToStandardIso(),
                    endDate = endDate?.formatToStandardIso(),
                    isAsc = sort?.let { it is SortType.ASC } ?: false,
                )
            }
            nextPageToken = if (news.isEmpty()) null else nextPageToken
            val newsToReturn = news.map { it.toNewsInfo(images = newsLocalDataSource.getImagesRelatedToNews(newsId = it.id)) }
            Resource.Success(data = NewsResponse(nextPageToken = nextPageToken, news = newsToReturn))
        } catch (e: SQLException) {
            e.printStackTrace()
            Resource.Error(message = e.message ?: "Something Went Wrong")
        } catch (e: Exception) {
            e.printStackTrace()
            val cachedNewsToReturn = if (cleanCache && fetchFromRemote)
                newsLocalDataSource.getNews(
                    symbols = symbols,
                    limit = limit ?: LIMIT_NEWS,
                    offset = offset,
                    startDate = startDate?.formatToStandardIso(),
                    endDate = endDate?.formatToStandardIso(),
                    isAsc = sort?.let { it is SortType.ASC } ?: false,
                )
            else null

            val nextPageToken = cachedNewsToReturn?.takeIf { it.isNotEmpty() }?.let { DEFAULT_PAGINATION_TOKEN_REQUEST_NEWS }
            val dataToReturn = cachedNewsToReturn?.let { newsListEntity ->
                NewsResponse(
                    nextPageToken = nextPageToken,
                    news = newsListEntity.map {
                        it.toNewsInfo(images = newsLocalDataSource.getImagesRelatedToNews(newsId = it.id))
                    }
                )
            }
            Resource.Error(message = e.message ?: "Something Went Wrong", data = dataToReturn)
        }
    }
}