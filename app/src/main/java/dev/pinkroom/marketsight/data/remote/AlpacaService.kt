package dev.pinkroom.marketsight.data.remote

import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import dev.pinkroom.marketsight.data.remote.model.dto.request.MessageAlpacaServiceDto
import kotlinx.coroutines.flow.Flow

interface AlpacaService {
    @Receive
    fun observeOnConnectionEvent(): Flow<WebSocket.Event>

    @Send
    fun sendMessage(message: MessageAlpacaServiceDto)

    @Receive
    fun observeResponse(): Flow<List<Any>>
}