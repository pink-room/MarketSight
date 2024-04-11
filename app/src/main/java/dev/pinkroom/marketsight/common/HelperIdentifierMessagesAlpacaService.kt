package dev.pinkroom.marketsight.common

import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.ErrorMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.NewsMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.SubscriptionMessageDto

sealed class HelperIdentifierMessagesAlpacaService<T>(
    val identifier: String,
    val classOfT: Class<T>? = null,
){
    data object News: HelperIdentifierMessagesAlpacaService<NewsMessageDto>(identifier = "n", classOfT = NewsMessageDto::class.java)
    data object Stocks: HelperIdentifierMessagesAlpacaService<Any>(identifier = "q")
    data object Crypto: HelperIdentifierMessagesAlpacaService<Any>(identifier = "t")
    data object Success: HelperIdentifierMessagesAlpacaService<Any>(identifier = "success")
    data object Error: HelperIdentifierMessagesAlpacaService<ErrorMessageDto>(identifier = "error", classOfT = ErrorMessageDto::class.java)
    data object Subscription: HelperIdentifierMessagesAlpacaService<SubscriptionMessageDto>(identifier = "subscription", classOfT = SubscriptionMessageDto::class.java)
}
