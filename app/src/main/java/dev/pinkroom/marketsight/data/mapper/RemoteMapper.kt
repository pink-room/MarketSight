package dev.pinkroom.marketsight.data.mapper

import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.BarAssetDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.QuoteAssetDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.QuotesResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.TradeAssetDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.TradesResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_crypto_api.QuotesCryptoResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_crypto_api.TradesCryptoResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.ImagesNewsDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.NewsDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.NewsResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.ErrorMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.NewsMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.SubscriptionMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_paper_api.AssetDto
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.bars_asset.BarAsset
import dev.pinkroom.marketsight.domain.model.common.ErrorMessage
import dev.pinkroom.marketsight.domain.model.common.SubscriptionMessage
import dev.pinkroom.marketsight.domain.model.news.ImageSize
import dev.pinkroom.marketsight.domain.model.news.ImagesNews
import dev.pinkroom.marketsight.domain.model.news.NewsInfo
import dev.pinkroom.marketsight.domain.model.news.NewsResponse
import dev.pinkroom.marketsight.domain.model.quotes_asset.QuoteAsset
import dev.pinkroom.marketsight.domain.model.quotes_asset.QuotesResponse
import dev.pinkroom.marketsight.domain.model.trades_asset.TradeAsset
import dev.pinkroom.marketsight.domain.model.trades_asset.TradesResponse
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

fun NewsMessageDto.toNewsInfo() = NewsInfo(
    author = this.author,
    createdAt = this.createdAt.toLocalDateTime(),
    headline = this.headline,
    id = this.id,
    source = this.source,
    summary = this.summary,
    symbols = this.symbols,
    updatedAt = this.updatedAt.toLocalDateTime(),
    url = this.url,
)

fun ErrorMessageDto.toErrorMessage() = ErrorMessage(
    code = this.code,
    msg = this.msg,
)

fun SubscriptionMessageDto.toSubscriptionMessage() = SubscriptionMessage(
    news = this.news,
    quotes = this.quotes,
    trades = this.trades,
    bars = this.bars,
)


fun String.toLocalDateTime(): LocalDateTime {
    val parser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    val date = try {
        LocalDateTime.parse(this, parser)
    } catch (e: Exception) { LocalDateTime.now() }
    return date.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
}

fun String.toLocalDateTimeWithNanoSecond(): LocalDateTime {
    val max = this.count().coerceAtMost(22)
    val formatStringDate = this.subSequence(0,max)
    val patternToUse = if (max<21) "yyyy-MM-dd'T'HH:mm:ss'Z'" else "yyyy-MM-dd'T'HH:mm:ss.SS"
    val parser = DateTimeFormatter.ofPattern(patternToUse, Locale.getDefault())
    val date = try {
        LocalDateTime.parse(formatStringDate, parser)
    } catch (e: Exception) { LocalDateTime.now() }
    return date.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
}

fun NewsResponseDto.toNewsResponse() = NewsResponse(
    news = this.news.map { it.toNewsInfo() },
    nextPageToken = this.nextPageToken,
)

fun NewsDto.toNewsInfo() = NewsInfo(
    author = this.author,
    createdAt = this.createdAt.toLocalDateTime(),
    headline = this.headline,
    id = this.id,
    source = this.source,
    summary = this.summary,
    symbols = this.symbols,
    updatedAt = this.updatedAt.toLocalDateTime(),
    url = this.url,
    images = this.images.map { it.toImagesNews() }
)

fun ImagesNewsDto.toImagesNews() = ImagesNews(
    url = this.url,
    size = this.size.toImageSize(),
)


fun String.toImageSize() = when(this) {
    "large" -> ImageSize.Large
    "small" -> ImageSize.Small
    else -> ImageSize.Thumb
}


fun AssetDto.toAsset() = Asset(
    id = id,
    name = name,
    symbol = symbol,
    exchange = exchange,
    isStock = type != "crypto",
)

fun BarAssetDto.toBarAsset() = BarAsset(
    openingPrice = openingPrice,
    closingPrice = closingPrice,
    highPrice = highPrice,
    barVolume = barVolume,
    lowPrice = lowPrice,
    tradeCountInBar = tradeCountInBar,
    timestamp = timestamp.toLocalDateTimeWithNanoSecond(),
    volumeWeightedAvgPrice = volumeWeightedAvgPrice,
    symbol = symbol,
)

fun QuotesResponseDto.toQuotesResponse() = QuotesResponse(
    quotes = quotes?.map { it.toQuoteAsset() } ?: emptyList(),
    pageToken = pageToken,
)

fun QuoteAssetDto.toQuoteAsset() = QuoteAsset(
    bidPrice = bidPrice,
    bidSize = bidSize,
    askPrice = askPrice,
    askSize = askSize,
    timeStamp = requestDate.toLocalDateTimeWithNanoSecond(),
    symbol = symbol,
)

fun QuotesCryptoResponseDto.toQuotesResponseDto() = QuotesResponseDto(
    quotes = quotes.entries.first().value,
    pageToken = pageToken,
    symbol = quotes.entries.first().key,
)

fun TradesResponseDto.toTradesResponse() = TradesResponse(
    trades = trades?.map { it.toTradeAsset() } ?: emptyList(),
    pageToken = pageToken,
)

fun TradeAssetDto.toTradeAsset() = TradeAsset(
    tradeId = tradeId,
    tradePrice = tradePrice,
    tradeSize = tradeSize,
    timeStamp = dateTransaction.toLocalDateTimeWithNanoSecond(),
    symbol = symbol,
)

fun TradesCryptoResponseDto.toTradesResponseDto() = TradesResponseDto(
    trades = trades.entries.first().value,
    pageToken = pageToken,
    symbol = trades.entries.first().key,
)