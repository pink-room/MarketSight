package dev.pinkroom.marketsight.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.pinkroom.marketsight.data.local.dao.AssetDao
import dev.pinkroom.marketsight.data.local.entity.AssetEntity

@Database(
    entities = [AssetEntity::class],
    version = 1,
)
abstract class MarketSightDatabase: RoomDatabase() {
    abstract val assetDao: AssetDao
}