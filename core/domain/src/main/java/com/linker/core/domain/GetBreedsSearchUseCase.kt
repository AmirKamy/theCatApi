package com.linker.core.domain

import Breed
import androidx.paging.PagingData
import com.example.core.data.repository.BreedRepository
import com.example.linker.core.model.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBreedsSearchUseCase @Inject constructor(private val repository: BreedRepository) {

    suspend operator fun invoke(query: String): Flow<Resource<List<Breed>>> {

        return repository.getBreedSearchResults(query)

    }
}