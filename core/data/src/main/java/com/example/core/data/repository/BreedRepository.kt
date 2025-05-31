package com.example.core.data.repository

import Breed
import androidx.paging.PagingData
import com.example.linker.core.model.Resource
import kotlinx.coroutines.flow.Flow

interface BreedRepository {

    fun getBreeds(): Flow<PagingData<Breed>>

    suspend fun toggleFavorite(breedId: String, isFavorite: Boolean)



}

