package dev.pinkroom.marketsight.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.pinkroom.marketsight.data.local.entity.ImagesEntity

@Dao
interface ImagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(images: List<ImagesEntity>)

    @Query(
        """
            SELECT url, size 
            FROM ImagesEntity 
            JOIN NewsImagesCrossRefEntity ON ImagesEntity.url LIKE NewsImagesCrossRefEntity.imageId 
            WHERE NewsImagesCrossRefEntity.newsId = :newsId        
        """
    )
    suspend fun getImagesRelatedToNews(newsId: Long): List<ImagesEntity>

    @Query("DELETE FROM ImagesEntity")
    suspend fun clearAll()
}