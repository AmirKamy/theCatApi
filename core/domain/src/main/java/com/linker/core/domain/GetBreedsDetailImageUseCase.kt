package com.linker.core.domain

import androidx.paging.PagingData
import com.example.core.data.repository.BreedRepository
import com.example.core.data.repository.BreedsDetailRepository
import com.example.linker.core.model.Image
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBreedsDetailImageUseCase @Inject constructor(private val repository: BreedsDetailRepository) {

    operator fun invoke(breedId: String): Flow<PagingData<Image>> {
        return repository.getBreedDetailImages(breedId)
    }

}