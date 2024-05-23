package dev.pinkroom.marketsight

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp
import dev.pinkroom.marketsight.common.WebSocketLifecycle
import javax.inject.Inject

@HiltAndroidApp
class MarketSightApp: Application() {
    @Inject
    internal lateinit var lifecycle: WebSocketLifecycle

    override fun onCreate() {
        super.onCreate()
        initWebSocketLifecycleObserver()
    }

    private fun initWebSocketLifecycleObserver() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycle)
    }
}