package dev.pinkroom.marketsight.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.pinkroom.marketsight.data.local.dao.MarketSightDao

@Database(
    entities = [],
    version = 1,
)
abstract class MarketSightDatabase: RoomDatabase() {
    abstract val dao: MarketSightDao
}