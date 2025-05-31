package com.example.linker.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.linker.core.database.dao.BreedDetailImageDao
import com.example.linker.core.database.dao.BreedsDao
import com.example.linker.core.database.model.BreedEntity
import com.example.linker.core.database.model.DetailImageEntity
import com.example.linker.core.database.model.FavoriteEntity
import com.example.linker.core.database.model.ImageEntity

@Database(
    entities = [BreedEntity::class, FavoriteEntity::class, ImageEntity::class, DetailImageEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun breedsDao(): BreedsDao
    abstract fun breedsDetailImageDao(): BreedDetailImageDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}