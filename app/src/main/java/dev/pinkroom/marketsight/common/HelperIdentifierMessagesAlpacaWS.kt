package dev.pinkroom.marketsight.common

import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.BarAssetDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.QuoteAssetDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.TradeAssetDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.ErrorMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.NewsMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.SubscriptionMessageDto

sealed class HelperIdentifierMessagesAlpacaWS<T>(
    val identifier: String,
    val classOfT: Class<T>? = null,
){
    data object News: HelperIdentifierMessagesAlpacaWS<NewsMessageDto>(identifier = "n", classOfT = NewsMessageDto::class.java)
    data object Trades: HelperIdentifierMessagesAlpacaWS<TradeAssetDto>(identifier = "t", classOfT = TradeAssetDto::class.java)
    data object Quotes: HelperIdentifierMessagesAlpacaWS<QuoteAssetDto>(identifier = "q", classOfT = QuoteAssetDto::class.java)
    data object Bars: HelperIdentifierMessagesAlpacaWS<BarAssetDto>(identifier = "b", classOfT = BarAssetDto::class.java)
    data object Success: HelperIdentifierMessagesAlpacaWS<Any>(identifier = "success")
    data object Error: HelperIdentifierMessagesAlpacaWS<ErrorMessageDto>(identifier = "error", classOfT = ErrorMessageDto::class.java)
    data object Subscription: HelperIdentifierMessagesAlpacaWS<SubscriptionMessageDto>(identifier = "subscription", classOfT = SubscriptionMessageDto::class.java)
}
