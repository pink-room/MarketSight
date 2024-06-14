package dev.pinkroom.marketsight.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImagesEntity(
    @PrimaryKey val url: String,
    val size: String,
)