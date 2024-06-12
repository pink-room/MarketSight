package dev.pinkroom.marketsight.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.pinkroom.marketsight.common.Constants.NEWS_ENTITY_NAME
import dev.pinkroom.marketsight.common.Constants.SYMBOL_COLUMN_NAME_NEWS_ENTITY
import dev.pinkroom.marketsight.common.Constants.UPDATE_AT_COLUMN_NAME_NEWS_ENTITY

@Entity(
    tableName = NEWS_ENTITY_NAME
)
data class NewsEntity(
    @PrimaryKey val id: Long,
    val headline: String,
    val summary: String,
    val author: String,
    val createdAt: String,
    @ColumnInfo(name = UPDATE_AT_COLUMN_NAME_NEWS_ENTITY) val updatedAt: String,
    val url: String,
    @ColumnInfo(name = SYMBOL_COLUMN_NAME_NEWS_ENTITY) val symbols: List<String>,
    val source: String,
)
