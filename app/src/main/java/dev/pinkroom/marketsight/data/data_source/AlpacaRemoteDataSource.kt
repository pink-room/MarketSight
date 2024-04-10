package dev.pinkroom.marketsight.data.data_source

import android.util.Log
import com.google.gson.Gson
import com.tinder.scarlet.Message
import com.tinder.scarlet.WebSocket
import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.HelperIdentifierMessagesAlpacaService
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.toObject
import dev.pinkroom.marketsight.common.verifyIfIsError
import dev.pinkroom.marketsight.data.mapper.toErrorMessage
import dev.pinkroom.marketsight.data.mapper.toNewsInfo
import dev.pinkroom.marketsight.data.mapper.toSubscriptionMessage
import dev.pinkroom.marketsight.data.remote.AlpacaService
import dev.pinkroom.marketsight.data.remote.model.dto.NewsMessageDto
import dev.pinkroom.marketsight.data.remote.model.request.MessageAlpacaService
import dev.pinkroom.marketsight.domain.model.NewsInfo
import dev.pinkroom.marketsight.domain.model.SubscriptionMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class AlpacaRemoteDataSource @Inject constructor(
    private val gson: Gson,
    private val alpacaService: AlpacaService,
    private val dispatchers: DispatcherProvider,
) {
    fun subscribeNews(symbols: List<String> = listOf("*")): Flow<Resource<WebSocket.Event>> = flow<Resource<WebSocket.Event>> {
        alpacaService.observeOnConnectionEvent().collect{
            when(it){
                is WebSocket.Event.OnConnectionOpened<*> -> {
                    alpacaService.sendSubscribe(message = MessageAlpacaService(action = ActionAlpaca.Subscribe.action, news = symbols))
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

    fun getRealTimeNews(): Flow<Resource<List<NewsInfo>>> = flow {
        alpacaService.observeResponse().collect{ data ->
            Log.d("TESTE","AQUI SEMPRE")
            val listNews = mutableListOf<NewsMessageDto>()
            data.forEach {
                gson.toObject(value = it, helperIdentifier = HelperIdentifierMessagesAlpacaService.News)?.let { news ->
                    listNews.add(news)
                }
            }
            emit(Resource.Success(listNews.map { it.toNewsInfo() }))
        }
    }.flowOn(dispatchers.IO)

    fun sendMessageToAlpacaService(message: MessageAlpacaService): Flow<Resource<SubscriptionMessage>> = flow<Resource<SubscriptionMessage>> {
        alpacaService.sendSubscribe(message = message)
        alpacaService.observeResponse().collect { data ->
            data.forEach {
                gson.toObject(value = it, helperIdentifier = HelperIdentifierMessagesAlpacaService.Subscription)?.let { sub ->
                    emit(Resource.Success(sub.toSubscriptionMessage()))
                } ?: run {
                    emit(Resource.Error(message = "Something went wrong on subscribe"))
                }
            }
        }
    }.flowOn(dispatchers.IO).take(1)

    companion object {
        const val TAG = "AlpacaRemote"
    }
}