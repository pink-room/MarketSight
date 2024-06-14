package dev.pinkroom.marketsight.util

import dev.pinkroom.marketsight.common.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class TestDispatcherProvider(
    private val testDispatcher: CoroutineDispatcher = Dispatchers.Main
) : DispatcherProvider {
    override val Main: CoroutineDispatcher
        get() = testDispatcher
    override val IO: CoroutineDispatcher
        get() = testDispatcher
    override val Default: CoroutineDispatcher
        get() = testDispatcher
}