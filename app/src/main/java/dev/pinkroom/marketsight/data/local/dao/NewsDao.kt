package dev.pinkroom.marketsight.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import dev.pinkroom.marketsight.data.local.entity.NewsEntity
import dev.pinkroom.marketsight.data.local.entity.NewsImagesCrossRefEntity

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(news: List<NewsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsImagesCrossRef(ref: List<NewsImagesCrossRefEntity>)

    @RawQuery
    suspend fun getNews(query: SupportSQLiteQuery): List<NewsEntity>

    @Query("DELETE FROM NewsEntity")
    suspend fun clearAll()

    @Query("DELETE FROM NewsImagesCrossRefEntity")
    suspend fun clearAllCrossRefNewsImages()
}