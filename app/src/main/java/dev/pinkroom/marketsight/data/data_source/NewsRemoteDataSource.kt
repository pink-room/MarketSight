package dev.pinkroom.marketsight.data.data_source

import com.google.gson.Gson
import com.tinder.scarlet.WebSocket
import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.Constants
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.HelperIdentifierMessagesAlpacaService
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.common.formatToStandardIso
import dev.pinkroom.marketsight.common.toObject
import dev.pinkroom.marketsight.data.remote.AlpacaNewsApi
import dev.pinkroom.marketsight.data.remote.AlpacaService
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.NewsResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.NewsMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.SubscriptionMessageDto
import dev.pinkroom.marketsight.data.remote.model.request.MessageAlpacaService
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import java.time.LocalDateTime
import javax.inject.Inject

class NewsRemoteDataSource @Inject constructor(
    private val gson: Gson,
    private val alpacaService: AlpacaService,
    private val alpacaNewsApi: AlpacaNewsApi,
    private val dispatchers: DispatcherProvider,
) {
    private var isNewsSubscribed: Boolean = false

    private suspend fun subscribeNews(symbols: List<String> = listOf("*")){
        alpacaService.observeOnConnectionEvent()
            .filter { it is WebSocket.Event.OnConnectionOpened<*> }
            .take(1)
            .collect{
                isNewsSubscribed = true
                alpacaService.sendMessage(
                    message = MessageAlpacaService(
                        action = ActionAlpaca.Subscribe.action, news = symbols,
                    )
                )
            }
    }

    fun getRealTimeNews() = flow {
        if (!isNewsSubscribed){
            subscribeNews()
        }
        alpacaService.observeResponse().collect{ data ->
            val listNews = mutableListOf<NewsMessageDto>()
            data.forEach {
                gson.toObject(value = it, helperIdentifier = HelperIdentifierMessagesAlpacaService.News)?.let { news ->
                    listNews.add(news)
                }
            }
            if (listNews.isNotEmpty()) emit(listNews.toList())
        }
    }.flowOn(dispatchers.IO)

    fun sendSubscribeMessageToAlpacaService(message: MessageAlpacaService) = flow<Resource<SubscriptionMessageDto>> {
        alpacaService.sendMessage(message = message)
        alpacaService.observeResponse().collect { data ->
            data.forEach {
                gson.toObject(value = it, helperIdentifier = HelperIdentifierMessagesAlpacaService.Subscription)?.let { sub ->
                    emit(Resource.Success(sub))
                } ?: run {
                    emit(Resource.Error(message = "Something went wrong on subscribe"))
                }
            }
        }
    }.flowOn(dispatchers.IO).take(1)

    suspend fun getNews(
        symbols: List<String>? = null,
        limit: Int? = Constants.LIMIT_NEWS,
        pageToken: String?,
        sort: SortType? = null,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
    ): NewsResponseDto {
        return alpacaNewsApi.getNews(
            symbols = symbols?.joinToString(","),
            perPage = limit,
            pageToken = pageToken,
            sort = sort?.type,
            startDate = startDate?.formatToStandardIso(),
            endDate = endDate?.formatToStandardIso(),
        )
    }
}