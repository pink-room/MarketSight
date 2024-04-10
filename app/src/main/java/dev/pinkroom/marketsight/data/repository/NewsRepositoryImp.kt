package dev.pinkroom.marketsight.data.repository

import android.util.Log
import com.tinder.scarlet.WebSocket
import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.data.data_source.AlpacaRemoteDataSource
import dev.pinkroom.marketsight.data.remote.model.request.MessageAlpacaService
import dev.pinkroom.marketsight.domain.model.NewsInfo
import dev.pinkroom.marketsight.domain.model.SubscriptionMessage
import dev.pinkroom.marketsight.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NewsRepositoryImp @Inject constructor(
    private val remoteDataSource: AlpacaRemoteDataSource,
    private val dispatchers: DispatcherProvider,
): NewsRepository {
    override fun subscribeNews(
        symbols: List<String>,
    ): Flow<Resource<WebSocket.Event>> = flow {
        remoteDataSource.subscribeNews(symbols = symbols).collect{
            emit(it)
        }
    }.flowOn(dispatchers.IO)

    override fun getRealTimeNews(): Flow<Resource<List<NewsInfo>>> = flow {
        remoteDataSource.getRealTimeNews().collect{
            emit(it)
        }
    }.flowOn(dispatchers.IO)

    override fun changeFilterNews(
        symbolsToSubscribe: List<String>?,
        symbolsToUnsubscribe: List<String>?
    ): Flow<Resource<SubscriptionMessage>> = flow<Resource<SubscriptionMessage>> {
        remoteDataSource.sendMessageToAlpacaService(message = MessageAlpacaService(action = ActionAlpaca.Subscribe.action, news = listOf("*"))).collect{

        }
    }.flowOn(dispatchers.IO)
}