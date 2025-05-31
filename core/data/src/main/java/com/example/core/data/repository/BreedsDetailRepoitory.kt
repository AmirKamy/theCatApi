package com.example.core.data.repository

import androidx.paging.PagingData
import com.example.linker.core.model.Image
import kotlinx.coroutines.flow.Flow

interface BreedsDetailRepository {

    fun getBreedDetailImages(breedId: String): Flow<PagingData<Image>>


}