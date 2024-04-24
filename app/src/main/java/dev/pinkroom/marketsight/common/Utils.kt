package dev.pinkroom.marketsight.common

import androidx.annotation.StringRes
import dev.pinkroom.marketsight.BuildConfig
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.Constants.ALL_SYMBOLS
import dev.pinkroom.marketsight.domain.model.common.SubInfoSymbols
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

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

fun LocalDate.toEpochMillis(zoneOffset: ZoneOffset = ZoneOffset.UTC, endOfTheDay: Boolean = false): Long{
    val time = if (endOfTheDay) atTime(23,59) else atStartOfDay()
    return time.atOffset(zoneOffset).toInstant().toEpochMilli()
}
fun LocalDate.toReadableDate(): String {
    val formatter = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.SHORT)
        .withLocale(Locale.getDefault())
    return format(formatter)
}

sealed class ActionAlpaca(val action: String) {
    data object Subscribe: ActionAlpaca(action = "subscribe")
    data object Unsubscribe: ActionAlpaca(action = "unsubscribe")
}

sealed class SortType(val type: String, @StringRes val stringId: Int){
    data object DESC: SortType(type = "desc", stringId = R.string.descending)
    data object ASC: SortType(type = "asc", stringId = R.string.ascending)
}

sealed interface DateMomentType{
    data object Start: DateMomentType
    data object End: DateMomentType
}

val popularSymbols = listOf(
    SubInfoSymbols(
        stringResource = R.string.all,
        name = ALL_SYMBOLS,
        symbol = "*",
        isSubscribed = true,
    ),
    SubInfoSymbols(
        name = "Tesla",
        symbol = "TSLA",
        isSubscribed = false,
    ),
    SubInfoSymbols(
        name = "Apple",
        symbol = "AAPL",
        isSubscribed = false,
    ),
    SubInfoSymbols(
        name = "Microsoft",
        symbol = "MSFT",
        isSubscribed = false,
    ),
    SubInfoSymbols(
        name = "NVIDIA",
        symbol = "NVDA",
        isSubscribed = false,
    ),
    SubInfoSymbols(
        name = "Alphabet",
        symbol = "GOOG",
        isSubscribed = false,
    ),
    SubInfoSymbols(
        name = "Amazon",
        symbol = "AMZN",
        isSubscribed = false,
    ),
    SubInfoSymbols(
        name = "Meta",
        symbol = "META",
        isSubscribed = false,
    ),
    SubInfoSymbols(
        name = "Visa",
        symbol = "V",
        isSubscribed = false,
    ),
    SubInfoSymbols(
        name = "Coca-Cola",
        symbol = "KO",
        isSubscribed = false,
    ),
    SubInfoSymbols(
        name = "Bitcoin",
        symbol = "BTC/USD",
        isSubscribed = false,
    ),
    SubInfoSymbols(
        name = "Ethereum",
        symbol = "ETH/USD",
        isSubscribed = false,
    ),
    SubInfoSymbols(
        name = "Shiba",
        symbol = "SHIB/USD",
        isSubscribed = false,
    ),
)