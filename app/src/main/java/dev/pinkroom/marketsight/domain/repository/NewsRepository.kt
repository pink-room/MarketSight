package dev.pinkroom.marketsight.domain.repository

import com.tinder.scarlet.WebSocket
import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.domain.model.NewsInfo
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun subscribeNews(
        symbols: List<String> = listOf("*"),
    ): Flow<Resource<WebSocket.Event>>
    fun getRealTimeNews(): Flow<List<NewsInfo>>
    suspend fun changeFilterNews(
        symbols: List<String>,
        actionAlpaca: ActionAlpaca,
    ): Resource<List<String>>
}