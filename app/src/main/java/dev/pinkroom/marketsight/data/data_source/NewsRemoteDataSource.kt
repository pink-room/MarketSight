package dev.pinkroom.marketsight.data.data_source

import android.util.Log
import com.google.gson.Gson
import com.tinder.scarlet.Message
import com.tinder.scarlet.WebSocket
import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.HelperIdentifierMessagesAlpacaService
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.common.toObject
import dev.pinkroom.marketsight.common.verifyIfIsError
import dev.pinkroom.marketsight.data.mapper.toErrorMessage
import dev.pinkroom.marketsight.data.remote.AlpacaNewsApi
import dev.pinkroom.marketsight.data.remote.AlpacaNewsService
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.NewsResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.NewsMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.SubscriptionMessageDto
import dev.pinkroom.marketsight.data.remote.model.request.MessageAlpacaService
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class NewsRemoteDataSource @Inject constructor(
    private val gson: Gson,
    private val alpacaNewsService: AlpacaNewsService,
    private val alpacaNewsApi: AlpacaNewsApi,
    private val dispatchers: DispatcherProvider,
) {
    fun subscribeNews(symbols: List<String> = listOf("*")) = flow<Resource<WebSocket.Event>> {
        alpacaNewsService.observeOnConnectionEvent().collect{
            when(it){
                is WebSocket.Event.OnConnectionOpened<*> -> {
                    alpacaNewsService.sendMessage(message = MessageAlpacaService(action = ActionAlpaca.Subscribe.action, news = symbols))
                }
                is WebSocket.Event.OnMessageReceived -> {
                    Log.d(TAG,"Received: ${it.message}")
                    if (it.message is Message.Text){
                        gson.verifyIfIsError(jsonValue = (it.message as Message.Text).value)?.let { errorMessage ->
                            emit(Resource.Error(message = errorMessage.msg, errorInfo = errorMessage.toErrorMessage()))
                        } ?: run {
                            emit(Resource.Success(it))
                        }
                    }
                }
                is WebSocket.Event.OnConnectionFailed -> {
                    emit(Resource.Error(message = it.throwable.message, data = it))
                }
                else -> Log.d(TAG,it.toString())
            }
        }
    }.flowOn(dispatchers.IO)

    fun getRealTimeNews() = flow {
        alpacaNewsService.observeResponse().collect{ data ->
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
        alpacaNewsService.sendMessage(message = message)
        alpacaNewsService.observeResponse().collect { data ->
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
        symbols: List<String>?,
        limit: Int?,
        pageToken: String?,
        sort: SortType?
    ): NewsResponseDto {
        return alpacaNewsApi.getNews(
            symbols = symbols?.joinToString(","),
            perPage = limit,
            pageToken = pageToken,
            sort = sort?.type,
        )
    }

    companion object {
        const val TAG = "AlpacaRemote"
    }
}