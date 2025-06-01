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
                    life_span = entity.breed.lifeSpan.toString(),
                    reference_image_id = entity.breed.referenceImageId.toString(),
                    isFavorite = entity.isFavorite,
                    imageUrl = entity.imageUrl,
                    imageWidth = entity.imageWidth,
                    imageHeight = entity.imageHeight
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

    override suspend fun getBreedSearchResults(query: String): Flow<List<Breed>> = withContext(
        Dispatchers.IO){
        if (query.isBlank()) {
            breedDao.getBreedSearchResults("")
        }

        try {

            val response = apiService.searchBread(query)

            if (response.isSuccessful) {
                val breeds = response.body()?.map { breedResponse ->
                    BreedEntity(
                        id = breedResponse.id,
                        name = breedResponse.name,
                        description = breedResponse.description,
                        origin = breedResponse.origin,
                        lifeSpan = breedResponse.life_span,
                        referenceImageId = breedResponse.reference_image_id
                    )
                } ?: emptyList()

                // ذخیره breeds
                if (breeds.isNotEmpty()) {
                    breedDao.insertBreeds(breeds)
                }

                // ذخیره تصاویر مرجع
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
            }
        } catch (e: Exception) {
            Log.e("SearchBreedsUseCase", "Error searching breeds: ${e.message}", e)
            // در حالت آفلاین، فقط از کش استفاده می‌شه
        }

        breedDao.getBreedSearchResults(query).map { breeds ->
            breeds.map { it.asExternalModel() }
        }

    }






}