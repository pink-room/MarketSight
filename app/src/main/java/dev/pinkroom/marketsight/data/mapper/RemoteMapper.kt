package dev.pinkroom.marketsight.data.mapper

import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_data_api.BarAssetDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_data_api.BarsCryptoResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_data_api.BarsStockResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_data_api.ImagesNewsDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_data_api.NewsDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_data_api.NewsResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.ErrorMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.NewsMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.SubscriptionMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_paper_api.AssetDto
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.common.ErrorMessage
import dev.pinkroom.marketsight.domain.model.common.SubscriptionMessage
import dev.pinkroom.marketsight.domain.model.historical_bars.BarAsset
import dev.pinkroom.marketsight.domain.model.news.ImageSize
import dev.pinkroom.marketsight.domain.model.news.ImagesNews
import dev.pinkroom.marketsight.domain.model.news.NewsInfo
import dev.pinkroom.marketsight.domain.model.news.NewsResponse
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
)


fun String.toLocalDateTime(): LocalDateTime {
    val parser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    val date = LocalDateTime.parse(this, parser)
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
    timestamp = timestamp.toLocalDateTime(),
    volumeWeightedAvgPrice = volumeWeightedAvgPrice,
)

fun BarsStockResponseDto.toListBarAsset() = bars.map { it.toBarAsset() }

fun BarsCryptoResponseDto.toListBarAsset() = bars.entries.first().value.map { it.toBarAsset() }