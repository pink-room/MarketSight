package dev.pinkroom.marketsight.data.remote.model.dto

import com.google.gson.annotations.SerializedName

data class NewsMessageDto(
    val id: Int,
    val headline: String,
    val summary: String,
    val author: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    val url: String,
    val content: String,
    val symbols: List<String>,
    val source: String,
)
