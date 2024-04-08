package dev.pinkroom.marketsight.di

import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.pinkroom.marketsight.BuildConfig
import dev.pinkroom.marketsight.common.FlowStreamAdapterFactory
import dev.pinkroom.marketsight.data.remote.AlpacaService
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val ALPACA_STREAM_URL_NEWS = BuildConfig.ALPACA_STREAM_URL + "v1beta1/news"
    private const val API_TIMEOUT = 60L
    @Provides
    @Singleton
    fun provideOkHttpClient() = OkHttpClient.Builder()
        .connectTimeout(API_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(API_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(API_TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()

            // Request customization: add request headers
            val requestBuilder = original.newBuilder()
                .addHeader("APCA-API-KEY-ID", BuildConfig.ALPACA_API_ID)
                .addHeader("APCA-API-SECRET-KEY", BuildConfig.ALPACA_API_SECRET)

            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()

    @Provides
    @Singleton
    fun provideAlpacaNewsService(okHttpClient: OkHttpClient): AlpacaService {
        val scarlet = Scarlet.Builder()
            .webSocketFactory(okHttpClient.newWebSocketFactory(ALPACA_STREAM_URL_NEWS))
            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
            .addStreamAdapterFactory(FlowStreamAdapterFactory())
            .build()

        return scarlet.create(AlpacaService::class.java)
    }
}