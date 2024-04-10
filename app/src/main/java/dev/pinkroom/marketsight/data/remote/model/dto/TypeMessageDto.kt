package dev.pinkroom.marketsight.data.remote.model.dto

import com.google.gson.annotations.SerializedName

data class TypeMessageDto(
    @SerializedName("T") val value: String,
)
