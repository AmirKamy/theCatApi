package com.example.core.data.repository

import Breed
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.core.data.BreedRemoteMediator
import com.example.linker.core.database.dao.BreedsDao
import com.example.linker.core.database.model.BreedEntity
import com.example.linker.core.database.model.FavoriteEntity
import com.example.linker.core.database.model.ImageEntity
import com.example.linker.core.database.model.asExternalModel
import com.example.linker.core.model.Resource
import com.linker.core.network.LinkerNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
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
                initialLoadSize = 10,
                prefetchDistance = 1
            ),
            remoteMediator = BreedRemoteMediator(apiService, breedDao),
            pagingSourceFactory = { breedDao.getBreeds() }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                Breed(
                    id = entity.breed.id,
                    name = entity.breed.name,
                    description = entity.breed.description.toString(),
                    origin = entity.breed.origin.toString(),
                    life_span = entity.breed.lifeSpan.toString(),
                    reference_image_id = entity.breed.referenceImageId.toString(),
                    isFavorite = entity.isFavorite,
                    imageUrl = entity.imageUrl,
                    imageWidth = entity.imageWidth,
                    temperament = entity.breed.temperament.toString(),
                    imageHeight = entity.imageHeight
                )
            }
        }
    }

    override suspend fun toggleFavorite(breedId: String, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                if (isFavorite) {
                    breedDao.addFavorite(FavoriteEntity(breedId))
                } else {
                    breedDao.removeFavorite(FavoriteEntity(breedId))
                }
            } catch (e: Exception) {
                Log.e("BreedRepository", "Error toggling favorite for breedId: $breedId, ${e.message}", e)
                throw e
            }
        }
    }

    override suspend fun getBreedSearchResults(query: String): Flow<Resource<List<Breed>>> = withContext(Dispatchers.IO) {
        try {
            if (query.isBlank()) {
                return@withContext breedDao.getBreedSearchResults("")
                    .map { breeds -> Resource.Success(breeds.map { it.asExternalModel() }) }
            }

            val response = apiService.searchBread(query)
            if (response.isSuccessful) {
                val breeds = response.body()?.map { breedResponse ->
                    BreedEntity(
                        id = breedResponse.id,
                        name = breedResponse.name,
                        description = breedResponse.description,
                        origin = breedResponse.origin,
                        lifeSpan = breedResponse.life_span,
                        temperament = breedResponse.temperament,
                        referenceImageId = breedResponse.reference_image_id
                    )
                } ?: emptyList()

                if (breeds.isNotEmpty()) {
                    breedDao.insertBreeds(breeds)
                }

                breeds.forEach { breed ->
                    breed.referenceImageId?.let { imageId ->
                        if (breedDao.getImageById(imageId) == null) {
                            try {
                                val imageResponse = apiService.getImage(imageId)
                                imageResponse.body()?.let { img ->
                                    breedDao.insertImage(
                                        ImageEntity(
                                            id = imageId,
                                            breedId = breed.id,
                                            url = img.url,
                                            width = img.width,
                                            height = img.height
                                        )
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e("SearchBreedsUseCase", "Error fetching image $imageId: ${e.message}", e)
                            }
                        }
                    }
                }

                breedDao.getBreedSearchResults(query)
                    .map { breeds -> Resource.Success(breeds.map { it.asExternalModel() }) }
            } else {
                breedDao.getBreedSearchResults(query)
                    .map { breeds ->
                        if (breeds.isNotEmpty()) {
                            Resource.Success(breeds.map { it.asExternalModel() })
                        } else {
                            Resource.Error("Failed to search breeds: Server error")
                        }
                    }
            }
        } catch (e: IOException) {
            breedDao.getBreedSearchResults(query)
                .map { breeds ->
                    if (breeds.isNotEmpty()) {
                        Resource.Success(breeds.map { it.asExternalModel() })
                    } else {
                        Resource.Error("No internet connection. Please check your network.")
                    }
                }
        } catch (e: HttpException) {
            breedDao.getBreedSearchResults(query)
                .map { breeds ->
                    if (breeds.isNotEmpty()) {
                        Resource.Success(breeds.map { it.asExternalModel() })
                    } else {
                        Resource.Error("Server error: ${e.message()}")
                    }
                }
        } catch (e: Exception) {
            breedDao.getBreedSearchResults(query)
                .map { breeds ->
                    if (breeds.isNotEmpty()) {
                        Resource.Success(breeds.map { it.asExternalModel() })
                    } else {
                        Resource.Error("An unexpected error occurred: ${e.message}")
                    }
                }
        }
    }
}