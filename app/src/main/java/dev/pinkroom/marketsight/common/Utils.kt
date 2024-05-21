package dev.pinkroom.marketsight.common

import androidx.annotation.StringRes
import dev.pinkroom.marketsight.BuildConfig
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.Constants.ALL_SYMBOLS
import dev.pinkroom.marketsight.domain.model.assets.AssetFilter
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.model.bars_asset.AssetChartInfo
import dev.pinkroom.marketsight.domain.model.bars_asset.BarAsset
import dev.pinkroom.marketsight.domain.model.bars_asset.FilterHistoricalBar
import dev.pinkroom.marketsight.domain.model.bars_asset.TimeFrame
import dev.pinkroom.marketsight.domain.model.common.DateTimeUnit
import dev.pinkroom.marketsight.domain.model.common.SubInfoSymbols
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.time.LocalDate
import java.time.LocalDateTime
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

fun LocalDateTime.toReadableDate(): String {
    val formatter = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.SHORT)
        .withLocale(Locale.getDefault())
    return format(formatter)
}

fun LocalDateTime.formatToStandardIso(): String = format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))

fun LocalDate.atEndOfTheDay(): LocalDateTime = atTime(23,59,59).atOffset(ZoneOffset.UTC).toLocalDateTime()

fun Double.formatToString(): String = if (toString().replace(".", "").length >= 7) {
    toLong().toString() // Convert to Long to drop decimal part
} else {
    String.format("%.2f", this)
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

val assetFilters = listOf(
    AssetFilter(
        typeAsset = TypeAsset.Stock,
        isSelected = true,
        stringId = R.string.stock,
        placeHolder = R.string.place_holder_stock,
    ),
    AssetFilter(
        typeAsset = TypeAsset.Crypto,
        isSelected = false,
        stringId = R.string.crypto,
        placeHolder = R.string.place_holder_crypto,
    ),
)

val historicalBarFilters = listOf(
    FilterHistoricalBar(
        value = 24,
        timeFrameIntervalValues = TimeFrame.Minutes(value = 5),
        timeFrameString = R.string.hour,
        dateTimeUnit = DateTimeUnit.Hour,
    ),
    FilterHistoricalBar(
        value = 7,
        timeFrameIntervalValues = TimeFrame.Minutes(value = 30),
        timeFrameString = R.string.day,
        dateTimeUnit = DateTimeUnit.Day,
    ),
    FilterHistoricalBar(
        value = 1,
        timeFrameIntervalValues = TimeFrame.Day,
        timeFrameString = R.string.month,
        dateTimeUnit = DateTimeUnit.Month,
    ),
    FilterHistoricalBar(
        value = 3,
        timeFrameIntervalValues = TimeFrame.Week,
        timeFrameString = R.string.month,
        dateTimeUnit = DateTimeUnit.Month,
    ),
    FilterHistoricalBar(
        value = 1,
        timeFrameIntervalValues = TimeFrame.Month(value = 1),
        timeFrameString = R.string.year,
        dateTimeUnit = DateTimeUnit.Year,
    ),
    FilterHistoricalBar(
        value = 20,
        timeFrameIntervalValues = TimeFrame.Month(value = 1),
        timeFrameString = R.string.all,
        dateTimeUnit = DateTimeUnit.All,
    ),
)

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
        symbol = "BTCUSD",
        isSubscribed = false,
    ),
    SubInfoSymbols(
        name = "Ethereum",
        symbol = "ETHUSD",
        isSubscribed = false,
    ),
    SubInfoSymbols(
        name = "Shiba",
        symbol = "SHIBUSD",
        isSubscribed = false,
    ),
)

fun mockChartData(): AssetChartInfo {
    val barsAsset = listOf(
        /*BarAsset(
            closingPrice = 429.29,
            timestamp = LocalDateTime.of(2020,7,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 705.64,
            timestamp = LocalDateTime.of(2020,10,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 668.01,
            timestamp = LocalDateTime.of(2021,1,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 679.83,
            timestamp = LocalDateTime.of(2021,4,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 775.01,
            timestamp = LocalDateTime.of(2021,7,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 1056.86,
            timestamp = LocalDateTime.of(2021,10,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 1077.77,
            timestamp = LocalDateTime.of(2022,1,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 674.5,
            timestamp = LocalDateTime.of(2022,4,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 265.11,
            timestamp = LocalDateTime.of(2023,7,1,4,0,0),
        ),*/
        /*BarAsset(
            closingPrice = 123.22,
            timestamp = LocalDateTime.of(2022,10,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 207.41,
            timestamp = LocalDateTime.of(2023,1,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 261.73,
            timestamp = LocalDateTime.of(2023,4,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 250.22,
            timestamp = LocalDateTime.of(2023,7,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 248.46,
            timestamp = LocalDateTime.of(2023,10,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 275.72,
            timestamp = LocalDateTime.of(2024,1,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 371.92,
            timestamp = LocalDateTime.of(2024,4,1,4,0,0),
        ),*/
        /*BarAsset(closingPrice = 261.73, timestamp = LocalDateTime.of(2023, 6, 1, 4, 0, 0)),
        BarAsset(closingPrice = 267.53, timestamp = LocalDateTime.of(2023, 7, 1, 4, 0, 0)),
        BarAsset(closingPrice = 258.06, timestamp = LocalDateTime.of(2023, 8, 1, 4, 0, 0)),
        BarAsset(closingPrice = 250.22, timestamp = LocalDateTime.of(2023, 9, 1, 4, 0, 0)),
        BarAsset(closingPrice = 200.88, timestamp = LocalDateTime.of(2023, 10, 1, 4, 0, 0)),
        BarAsset(closingPrice = 239.79, timestamp = LocalDateTime.of(2023, 11, 1, 4, 0, 0)),
        BarAsset(closingPrice = 248.46, timestamp = LocalDateTime.of(2023, 12, 1, 5, 0, 0)),
        BarAsset(closingPrice = 187.24, timestamp = LocalDateTime.of(2024, 1, 1, 5, 0, 0)),
        BarAsset(closingPrice = 201.81, timestamp = LocalDateTime.of(2024, 2, 1, 5, 0, 0)),
        BarAsset(closingPrice = 175.72, timestamp = LocalDateTime.of(2024, 3, 1, 5, 0, 0)),
        BarAsset(closingPrice = 183.34, timestamp = LocalDateTime.of(2024, 4, 1, 4, 0, 0)),
        BarAsset(closingPrice = 174.86, timestamp = LocalDateTime.of(2024, 5, 1, 4, 0, 0))*/
        BarAsset(closingPrice = 297.53, timestamp = LocalDateTime.of(2023, 7, 1, 4, 0, 0)),
        BarAsset(closingPrice = 297.53, timestamp = LocalDateTime.of(2023, 7, 2, 4, 0, 0)),
        BarAsset(closingPrice = 222298.53, timestamp = LocalDateTime.of(2024, 7, 2, 4, 0, 0)),
    )
    val maxValue = barsAsset.maxOfOrNull { it.closingPrice }!!
    val minValue = barsAsset.minOfOrNull { it.closingPrice }!!
    return AssetChartInfo(
        upperValue = maxValue,
        lowerValue = minValue,
        barsInfo = barsAsset,
    )
}