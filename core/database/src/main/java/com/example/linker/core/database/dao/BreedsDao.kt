package com.example.linker.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.linker.core.database.model.BreedEntity
import com.example.linker.core.database.model.FavoriteEntity

@Dao
interface BreedsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(breeds: List<BreedEntity>)

    @Query("""
        SELECT breeds.*, 
               CASE WHEN favorites.breedId IS NOT NULL THEN 1 ELSE 0 END AS isFavorite
        FROM breeds
        LEFT JOIN favorites ON breeds.id = favorites.breedId
    """)
    fun getBreeds(): PagingSource<Int, BreedWithFavorite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun removeFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM breeds")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM breeds")
    suspend fun getBreedCount(): Int
}

data class BreedWithFavorite(
    @Embedded val breed: BreedEntity,
    val isFavorite: Boolean
)