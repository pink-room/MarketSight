package dev.pinkroom.marketsight.di

import android.content.Context
import com.google.gson.Gson
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.pinkroom.marketsight.BuildConfig
import dev.pinkroom.marketsight.MarketSightApp
import dev.pinkroom.marketsight.common.DefaultDispatchers
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.FlowStreamAdapterFactory
import dev.pinkroom.marketsight.common.addAuthenticationInterceptor
import dev.pinkroom.marketsight.common.addLoggingInterceptor
import dev.pinkroom.marketsight.data.data_source.AlpacaRemoteDataSource
import dev.pinkroom.marketsight.data.remote.AlpacaNewsApi
import dev.pinkroom.marketsight.data.remote.AlpacaNewsService
import dev.pinkroom.marketsight.data.repository.NewsRepositoryImp
import dev.pinkroom.marketsight.domain.repository.NewsRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
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
        .addAuthenticationInterceptor()
        .addLoggingInterceptor()
        .build()

    @Provides
    @Singleton
    fun provideLifeCycle(@ApplicationContext context: Context): Lifecycle = AndroidLifecycle.ofApplicationForeground(
        context as MarketSightApp
    )

    @Provides
    @Singleton
    fun provideAlpacaNewsService(okHttpClient: OkHttpClient, lifecycle: Lifecycle): AlpacaNewsService {
        val scarlet = Scarlet.Builder()
            .webSocketFactory(okHttpClient.newWebSocketFactory(ALPACA_STREAM_URL_NEWS))
            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
            .addStreamAdapterFactory(FlowStreamAdapterFactory())
            //.lifecycle(lifecycle)
            .build()

        return scarlet.create(AlpacaNewsService::class.java)
    }

    @Provides
    @Singleton
    fun provideAlpacaNewsApi(okHttpClient: OkHttpClient): AlpacaNewsApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.ALPACA_DATA_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideDispatchers(): DispatcherProvider{
        return DefaultDispatchers()
    }

    @Provides
    @Singleton
    fun provideNewsRepository(alpacaRemoteDataSource: AlpacaRemoteDataSource, dispatcherProvider: DispatcherProvider): NewsRepository {
        return NewsRepositoryImp(remoteDataSource = alpacaRemoteDataSource, dispatchers = dispatcherProvider)
    }
}