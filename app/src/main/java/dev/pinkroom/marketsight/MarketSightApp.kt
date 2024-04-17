package dev.pinkroom.marketsight

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MarketSightApp: Application(), ImageLoaderFactory{
    override fun newImageLoader(): ImageLoader {
        return ImageLoader(this).newBuilder()
            .logger(DebugLogger())
            .build()
    }

}