package dev.pinkroom.marketsight.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AssetEntity(
    @PrimaryKey val id: String,
    val name: String,
    val symbol: String,
    val type: String,
    val exchange: String,
)
