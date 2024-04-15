package dev.pinkroom.marketsight.common

import com.tinder.scarlet.Stream
import com.tinder.scarlet.StreamAdapter
import com.tinder.scarlet.utils.getRawType
import kotlinx.coroutines.flow.Flow
import java.lang.reflect.Type
import kotlinx.coroutines.reactive.asFlow

class FlowStreamAdapterFactory : StreamAdapter.Factory {

    override fun create(type: Type): StreamAdapter<Any, Any> {
        return when (type.getRawType()) {
            Flow::class.java -> FlowStreamAdapter()
            else -> throw IllegalArgumentException()
        }
    }
}

private class FlowStreamAdapter<T> : StreamAdapter<T, Flow<T>> where T : Any {
    override fun adapt(stream: Stream<T>) = stream.asFlow()
}