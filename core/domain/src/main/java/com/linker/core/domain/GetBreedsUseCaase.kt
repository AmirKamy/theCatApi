package com.linker.core.domain

import Breed
import androidx.paging.PagingData
import com.example.core.data.repository.BreedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBreedsUseCase @Inject constructor(private val repository: BreedRepository) {
    operator fun invoke(): Flow<PagingData<Breed>> {

        return repository.getBreeds()

    }
}