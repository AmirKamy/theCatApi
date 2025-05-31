package com.example.core.data.di

import com.example.core.data.repository.BreedRepository
import com.example.core.data.repository.BreedRepositoryImpl
import com.example.core.data.repository.BreedsDetailRepository
import com.example.core.data.repository.BreedsDetailRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsBreedRepository(
        breedsRepository: BreedRepositoryImpl,
    ): BreedRepository

    @Binds
    internal abstract fun bindsBreedDetailRepository(
        breedDetailRepository: BreedsDetailRepositoryImpl,
    ): BreedsDetailRepository
}