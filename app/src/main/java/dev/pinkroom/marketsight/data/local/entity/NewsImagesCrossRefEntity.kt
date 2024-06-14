package dev.pinkroom.marketsight.data.local.entity

import androidx.room.Entity

@Entity(primaryKeys = ["newsId", "imageId"])
data class NewsImagesCrossRefEntity(
    val newsId: Long,
    val imageId: String,
)
