package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service

import com.google.gson.annotations.SerializedName

data class ErrorMessageDto(
    @SerializedName("T") val type: String,
    val code: Int,
    val msg: String,
)
