package dev.pinkroom.marketsight.common

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.ErrorMessageDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.TypeMessageDto
import java.lang.reflect.Type

fun <T> Gson.toJsonValue(value: Any?, classOfT: Class<T>): T = fromJson(toJson(value), classOfT)
inline fun <reified T> Gson.fromJson(value: String): T = fromJson(value, typeToken<T>())

inline fun <reified T> typeToken(): Type = object : TypeToken<T>() {}.type

fun <T> Gson.toObject(value: Any, helperIdentifier: HelperIdentifierMessagesAlpacaWS<T>): T? {
    val typeMessage = toJsonValue(value, TypeMessageDto::class.java)
    return if (typeMessage.value == helperIdentifier.identifier && helperIdentifier.classOfT != null){
        toJsonValue(value, helperIdentifier.classOfT)
    } else null
}

fun Gson.verifyIfIsError(value: Any): ErrorMessageDto? {
    val helperIdentifier = HelperIdentifierMessagesAlpacaWS.Error
    toObject(value = value, helperIdentifier = helperIdentifier)?.let {
        return it
    }
    return null
}
