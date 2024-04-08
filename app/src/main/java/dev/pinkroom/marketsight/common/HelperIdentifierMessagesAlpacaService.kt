package dev.pinkroom.marketsight.common

sealed class HelperIdentifierMessagesAlpacaService(
    val identifier: String,
){
    data object News: HelperIdentifierMessagesAlpacaService(identifier = "n")
    data object Stocks: HelperIdentifierMessagesAlpacaService(identifier = "q")
    data object Crypto: HelperIdentifierMessagesAlpacaService(identifier = "t")
    data object Success: HelperIdentifierMessagesAlpacaService(identifier = "success")
    data object Error: HelperIdentifierMessagesAlpacaService(identifier = "error")
    data object Subscription: HelperIdentifierMessagesAlpacaService(identifier = "subscription")
}
