package dev.pinkroom.marketsight.common

import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.BarAssetDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.QuoteAssetDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.TradeAssetDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.ErrorMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.NewsMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.SubscriptionMessageDto

sealed class HelperIdentifierMessagesAlpacaService<T>(
    val identifier: String,
    val classOfT: Class<T>? = null,
){
    data object News: HelperIdentifierMessagesAlpacaService<NewsMessageDto>(identifier = "n", classOfT = NewsMessageDto::class.java)
    data object Trades: HelperIdentifierMessagesAlpacaService<TradeAssetDto>(identifier = "t", classOfT = TradeAssetDto::class.java)
    data object Quotes: HelperIdentifierMessagesAlpacaService<QuoteAssetDto>(identifier = "q", classOfT = QuoteAssetDto::class.java)
    data object Bars: HelperIdentifierMessagesAlpacaService<BarAssetDto>(identifier = "b", classOfT = BarAssetDto::class.java)
    data object Success: HelperIdentifierMessagesAlpacaService<Any>(identifier = "success")
    data object Error: HelperIdentifierMessagesAlpacaService<ErrorMessageDto>(identifier = "error", classOfT = ErrorMessageDto::class.java)
    data object Subscription: HelperIdentifierMessagesAlpacaService<SubscriptionMessageDto>(identifier = "subscription", classOfT = SubscriptionMessageDto::class.java)
}
