package com.example.core.data.repository

import Breed
import android.annotation.SuppressLint
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.core.data.BreedRemoteMediator
import com.example.linker.core.database.dao.BreedsDao
import com.example.linker.core.database.model.FavoriteEntity
import com.linker.core.network.LinkerNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BreedRepositoryImpl @Inject constructor(
    private val apiService: LinkerNetworkDataSource,
    private val breedDao: BreedsDao
) : BreedRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getBreeds(): Flow<PagingData<Breed>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 10
            ),
            remoteMediator = BreedRemoteMediator(apiService, breedDao),
            pagingSourceFactory = { breedDao.getBreeds() }
        ).flow.map { pagingData ->
            Log.i("BreedRepository", "Mapping PagingData with $pagingData")
            pagingData.map { entity ->
                Breed(
                    id = entity.breed.id,
                    name = entity.breed.name,
                    description = entity.breed.description.toString(),
                    origin = entity.breed.origin.toString(),
                    lifeSpan = entity.breed.lifeSpan.toString(),
                    referenceImageId = entity.breed.referenceImageId.toString(),
                    isFavorite = entity.isFavorite
                )
            }
        }
    }

    override suspend fun toggleFavorite(breedId: String, isFavorite: Boolean) {
        Log.i("BreedRepository", "Toggling favorite for breedId: $breedId, isFavorite: $isFavorite")
        if (isFavorite) {
            breedDao.addFavorite(FavoriteEntity(breedId))
        } else {
            breedDao.removeFavorite(FavoriteEntity(breedId))
        }
    }
}