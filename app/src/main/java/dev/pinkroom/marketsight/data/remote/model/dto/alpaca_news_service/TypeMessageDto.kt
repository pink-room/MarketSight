package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service

import com.google.gson.annotations.SerializedName

data class TypeMessageDto(
    @SerializedName("T") val value: String,
)
