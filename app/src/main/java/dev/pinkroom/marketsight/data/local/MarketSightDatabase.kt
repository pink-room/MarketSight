package dev.pinkroom.marketsight.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.pinkroom.marketsight.data.local.converter.SymbolsConverter
import dev.pinkroom.marketsight.data.local.dao.AssetDao
import dev.pinkroom.marketsight.data.local.dao.ImagesDao
import dev.pinkroom.marketsight.data.local.dao.NewsDao
import dev.pinkroom.marketsight.data.local.entity.AssetEntity
import dev.pinkroom.marketsight.data.local.entity.ImagesEntity
import dev.pinkroom.marketsight.data.local.entity.NewsEntity
import dev.pinkroom.marketsight.data.local.entity.NewsImagesCrossRefEntity

@Database(
    entities = [
        AssetEntity::class, NewsEntity::class,
        ImagesEntity::class, NewsImagesCrossRefEntity::class,
    ],
    version = 1,
)
@TypeConverters(
    SymbolsConverter::class,
)
abstract class MarketSightDatabase: RoomDatabase() {
    abstract val assetDao: AssetDao
    abstract val newsDao: NewsDao
    abstract val imagesDao: ImagesDao
}