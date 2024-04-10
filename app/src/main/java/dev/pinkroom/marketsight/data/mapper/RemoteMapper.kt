package dev.pinkroom.marketsight.data.mapper

import dev.pinkroom.marketsight.data.remote.model.dto.ErrorMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.NewsMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.SubscriptionMessageDto
import dev.pinkroom.marketsight.domain.model.ErrorMessage
import dev.pinkroom.marketsight.domain.model.NewsInfo
import dev.pinkroom.marketsight.domain.model.SubscriptionMessage
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