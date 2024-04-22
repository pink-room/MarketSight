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
import dev.pinkroom.marketsight.common.connection_network.ConnectivityObserver
import dev.pinkroom.marketsight.common.connection_network.NetworkConnectivityObserver
import dev.pinkroom.marketsight.data.data_source.NewsRemoteDataSource
import dev.pinkroom.marketsight.data.remote.AlpacaNewsApi
import dev.pinkroom.marketsight.data.remote.AlpacaNewsService
import dev.pinkroom.marketsight.data.repository.NewsRepositoryImp
import dev.pinkroom.marketsight.domain.repository.NewsRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val ALPACA_STREAM_URL_NEWS = BuildConfig.ALPACA_STREAM_URL + "v1beta1/news"
    private const val API_TIMEOUT_WS = 30L
    private const val API_TIMEOUT_API = 10L
    private const val OK_HTTP_WS = "okHttpWS"
    private const val OK_HTTP_API = "okHttpAPI"

    @Provides
    @Singleton
    @Named(OK_HTTP_WS)
    fun provideOkHttpClientWS() = OkHttpClient.Builder()
        .connectTimeout(API_TIMEOUT_WS, TimeUnit.SECONDS)
        .readTimeout(API_TIMEOUT_WS, TimeUnit.SECONDS)
        .writeTimeout(API_TIMEOUT_WS, TimeUnit.SECONDS)
        .addAuthenticationInterceptor()
        .addLoggingInterceptor()
        .build()

    @Provides
    @Singleton
    @Named(OK_HTTP_API)
    fun provideOkHttpClientAPI() = OkHttpClient.Builder()
        .connectTimeout(API_TIMEOUT_API, TimeUnit.SECONDS)
        .readTimeout(API_TIMEOUT_API, TimeUnit.SECONDS)
        .writeTimeout(API_TIMEOUT_API, TimeUnit.SECONDS)
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
    fun provideAlpacaNewsService(
        @Named(OK_HTTP_WS) okHttpClient: OkHttpClient, lifecycle: Lifecycle
    ): AlpacaNewsService {
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
    fun provideAlpacaNewsApi(@Named(OK_HTTP_API) okHttpClient: OkHttpClient): AlpacaNewsApi {
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
    fun provideDispatchers(): DispatcherProvider {
        return DefaultDispatchers()
    }

    @Provides
    @Singleton
    fun provideNewsRepository(newsRemoteDataSource: NewsRemoteDataSource, dispatcherProvider: DispatcherProvider): NewsRepository {
        return NewsRepositoryImp(newsRemoteDataSource = newsRemoteDataSource, dispatchers = dispatcherProvider)
    }

    @Provides
    @Singleton
    fun provideConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver =
        NetworkConnectivityObserver(context = context)
}