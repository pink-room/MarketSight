package dev.pinkroom.marketsight.common

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.lifecycle.LifecycleRegistry

class WebSocketLifecycle(
    private val lifecycleRegistry: LifecycleRegistry,
) : Lifecycle by lifecycleRegistry, DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        lifecycleRegistry.onNext(Lifecycle.State.Started)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        lifecycleRegistry.onComplete()
    }
}