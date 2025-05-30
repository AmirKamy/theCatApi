package com.linker.core.domain

import Breed
import androidx.paging.PagingData
import com.example.core.data.repository.BreedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetFavoriteBreedsUseCase @Inject constructor(private val repository: BreedRepository) {
    suspend operator fun invoke(breedId: String, isFavorite: Boolean) {
        return repository.toggleFavorite(breedId, isFavorite)
    }
}