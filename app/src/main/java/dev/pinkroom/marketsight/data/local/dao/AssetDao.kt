package dev.pinkroom.marketsight.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.pinkroom.marketsight.data.local.entity.AssetEntity

@Dao
interface AssetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(assets: List<AssetEntity>)

    @Query("DELETE FROM assetentity WHERE type == :typeAsset")
    suspend fun clearAllOfType(typeAsset: String)

    @Query("SELECT * FROM assetentity WHERE type == :typeAsset")
    suspend fun getAllAssetsOfType(typeAsset: String): List<AssetEntity>
}