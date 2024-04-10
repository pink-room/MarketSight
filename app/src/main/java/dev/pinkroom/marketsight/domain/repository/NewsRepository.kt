package dev.pinkroom.marketsight.domain.repository

import com.tinder.scarlet.WebSocket
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.domain.model.NewsInfo
import dev.pinkroom.marketsight.domain.model.SubscriptionMessage
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun subscribeNews(
        symbols: List<String> = listOf("*"),
    ): Flow<Resource<WebSocket.Event>>
    fun getRealTimeNews(): Flow<Resource<List<NewsInfo>>>
    fun changeFilterNews(
        symbolsToSubscribe: List<String>? = null,
        symbolsToUnsubscribe: List<String>? = null,
    ): Flow<Resource<SubscriptionMessage>>
}