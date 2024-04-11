package dev.pinkroom.marketsight.data.remote

import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import dev.pinkroom.marketsight.data.remote.model.request.MessageAlpacaService
import kotlinx.coroutines.flow.Flow

interface AlpacaNewsService {
    @Receive
    fun observeOnConnectionEvent(): Flow<WebSocket.Event>

    @Send
    fun sendMessage(message: MessageAlpacaService)

    @Receive
    fun observeResponse(): Flow<List<Any>>
}