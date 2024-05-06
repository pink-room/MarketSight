package dev.pinkroom.marketsight.common

import com.google.gson.Gson
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.ErrorMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.TypeMessageDto

fun <T> Gson.toJsonValue(value: Any?, classOfT: Class<T>): T = fromJson(toJson(value), classOfT)

fun <T> Gson.toObject(value: Any, helperIdentifier: HelperIdentifierMessagesAlpacaService<T>): T? {
    val typeMessage = toJsonValue(value, TypeMessageDto::class.java)
    return if (typeMessage.value == helperIdentifier.identifier && helperIdentifier.classOfT != null){
        toJsonValue(value, helperIdentifier.classOfT)
    } else null
}

fun Gson.verifyIfIsError(value: Any): ErrorMessageDto? {
    val helperIdentifier = HelperIdentifierMessagesAlpacaService.Error
    toObject(value = value, helperIdentifier = helperIdentifier)?.let {
        return it
    }
    return null
}
