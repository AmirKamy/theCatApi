package com.example.linker.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.linker.core.database.model.BreedEntity
import com.example.linker.core.database.model.BreedWithFavorite
import com.example.linker.core.database.model.FavoriteEntity
import com.example.linker.core.database.model.ImageEntity

@Dao
interface BreedsDao {
    @Query(
        """
        SELECT breeds.*, 
               CASE WHEN favorites.breedId IS NOT NULL THEN 1 ELSE 0 END AS isFavorite,
               images.url AS imageUrl,
               images.width AS imageWidth,
               images.height AS imageHeight
        FROM breeds
        LEFT JOIN favorites ON breeds.id = favorites.breedId
        LEFT JOIN images ON breeds.referenceImageId = images.id
        ORDER BY breeds.id
    """
    )
    fun getBreeds(): PagingSource<Int, BreedWithFavorite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(breeds: List<BreedEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Query("DELETE FROM breeds")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun removeFavorite(favorite: FavoriteEntity)

    @Query("SELECT COUNT(*) FROM breeds")
    suspend fun getBreedCount(): Int

    @Query("SELECT * FROM images WHERE id = :imageId")
    suspend fun getImageById(imageId: String): ImageEntity?
}
