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
import kotlinx.coroutines.flow.Flow

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

    @Query("SELECT * FROM breeds WHERE id = :breedId")
    fun getBreed(breedId: String): BreedEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
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


    @Query("""
        SELECT b.id, b.name, b.description, b.origin, b.lifeSpan AS lifeSpan, 
               b.referenceImageId AS referenceImageId,
               CASE WHEN f.breedId IS NOT NULL THEN 1 ELSE 0 END AS isFavorite,
               i.url AS imageUrl, i.width AS imageWidth, i.height AS imageHeight
        FROM breeds b
        LEFT JOIN images i ON b.referenceImageId = i.id
        LEFT JOIN favorites f ON b.id = f.breedId
        WHERE b.name LIKE '%' || :query || '%'
    """)
    fun getBreedSearchResults(query: String): Flow<List<BreedWithFavorite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreeds(breeds: List<BreedEntity>)

}
