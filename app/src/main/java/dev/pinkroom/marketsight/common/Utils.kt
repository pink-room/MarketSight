package dev.pinkroom.marketsight.common

import dev.pinkroom.marketsight.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

fun OkHttpClient.Builder.addAuthenticationInterceptor(): OkHttpClient.Builder {
    addInterceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("APCA-API-KEY-ID", BuildConfig.ALPACA_API_ID)
            .addHeader("APCA-API-SECRET-KEY", BuildConfig.ALPACA_API_SECRET)

        val request = requestBuilder.build()
        chain.proceed(request)
    }
    return this
}

fun OkHttpClient.Builder.addLoggingInterceptor(): OkHttpClient.Builder {
    if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        addInterceptor(loggingInterceptor)
    }
    return this
}

sealed class ActionAlpaca(val action: String) {
    data object Subscribe: ActionAlpaca(action = "subscribe")
    data object Unsubscribe: ActionAlpaca(action = "unsubscribe")
}

sealed class SortType(val type: String){
    data object DESC: SortType(type = "desc")
    data object ASC: SortType(type = "asc")
}