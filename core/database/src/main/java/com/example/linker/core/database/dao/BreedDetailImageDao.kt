package com.example.linker.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.linker.core.database.model.DetailImageEntity

@Dao
interface BreedDetailImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetailImages(images: List<DetailImageEntity>)

    @Query("SELECT * FROM detail_images WHERE breedId = :breedId ORDER BY id")
    fun getDetailImages(breedId: String): PagingSource<Int, DetailImageEntity>

    @Query("DELETE FROM detail_images WHERE breedId = :breedId")
    suspend fun clearDetailImages(breedId: String)

    @Query("SELECT COUNT(*) FROM detail_images WHERE breedId = :breedId")
    suspend fun getDetailImageCount(breedId: String): Int
}