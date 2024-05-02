package dev.pinkroom.marketsight.data.remote.model.dto.alpaca_data_api

import com.google.gson.annotations.SerializedName

data class NewsDto(
    val id: Long,
    val headline: String,
    val summary: String,
    val author: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    val url: String,
    val symbols: List<String> = emptyList(),
    val source: String,
    val images: List<ImagesNewsDto> = emptyList(),
)
