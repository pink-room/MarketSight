package dev.pinkroom.marketsight.data.mapper

import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.ImagesNewsDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.NewsDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.NewsResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.ErrorMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.NewsMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.SubscriptionMessageDto
import dev.pinkroom.marketsight.domain.model.common.ErrorMessage
import dev.pinkroom.marketsight.domain.model.common.SubscriptionMessage
import dev.pinkroom.marketsight.domain.model.news.ImageSize
import dev.pinkroom.marketsight.domain.model.news.ImagesNews
import dev.pinkroom.marketsight.domain.model.news.NewsInfo
import dev.pinkroom.marketsight.domain.model.news.NewsResponse
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

fun NewsMessageDto.toNewsInfo(): NewsInfo {
    return NewsInfo(
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
}

fun ErrorMessageDto.toErrorMessage(): ErrorMessage {
    return ErrorMessage(
        code = this.code,
        msg = this.msg,
    )
}

fun SubscriptionMessageDto.toSubscriptionMessage(): SubscriptionMessage {
    return SubscriptionMessage(
        news = this.news,
        quotes = this.quotes,
        trades = this.trades,
    )
}

fun String.toLocalDateTime(): LocalDateTime {
    val parser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    val date = LocalDateTime.parse(this, parser)
    return date.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
}

fun NewsResponseDto.toNewsResponse(): NewsResponse {
    return NewsResponse(
        news = this.news.map { it.toNewsInfo() },
        nextPageToken = this.nextPageToken,
    )
}

fun NewsDto.toNewsInfo(): NewsInfo {
    return NewsInfo(
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
}

fun ImagesNewsDto.toImagesNews(): ImagesNews {
    return ImagesNews(
        url = this.url,
        size = this.size.toImageSize(),
    )
}

fun String.toImageSize(): ImageSize {
    return when(this){
        "large" -> ImageSize.Large
        "small" -> ImageSize.Small
        else -> ImageSize.Thumb
    }
}