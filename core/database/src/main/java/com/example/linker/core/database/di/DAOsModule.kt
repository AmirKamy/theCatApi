package com.example.linker.core.database.di


import com.example.linker.core.database.AppDatabase
import com.example.linker.core.database.dao.BreedDetailImageDao
import com.example.linker.core.database.dao.BreedsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DAOsModule {
    @Provides
    fun providesBreedsDao(
        database: AppDatabase,
    ): BreedsDao = database.breedsDao()

    @Provides
    fun providesBreedsDetailImageDao(
        database: AppDatabase,
    ): BreedDetailImageDao = database.breedsDetailImageDao()

}