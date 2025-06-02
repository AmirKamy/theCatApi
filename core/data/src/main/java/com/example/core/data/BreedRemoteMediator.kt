package com.example.core.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.linker.core.database.dao.BreedsDao
import com.example.linker.core.database.model.BreedEntity
import com.example.linker.core.database.model.BreedWithFavorite
import com.example.linker.core.database.model.ImageEntity
import com.linker.core.network.LinkerNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class BreedRemoteMediator(
    private val apiService: LinkerNetworkDataSource,
    private val breedDao: BreedsDao
) : RemoteMediator<Int, BreedWithFavorite>() {
    private var nextPage: Int? = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BreedWithFavorite>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                nextPage = 0
                0
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                nextPage ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        return try {
            val response = apiService.getBreeds(limit = 10, page = page)
            if (!response.isSuccessful) {
                val breedCount = breedDao.getBreedCount()
                return if (breedCount > 0) {
                    MediatorResult.Success(endOfPaginationReached = false)
                } else {
                    MediatorResult.Error(HttpException(response))
                }
            }

            val breeds = response.body()?.map {
                BreedEntity(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    origin = it.origin,
                    temperament = it.temperament,
                    lifeSpan = it.life_span,
                    referenceImageId = it.reference_image_id
                )
            } ?: emptyList()

            withContext(Dispatchers.IO) {
                breeds.filter { it.referenceImageId != null }
                    .map { breed ->
                        async {
                            val imageId = breed.referenceImageId!!
                            val existingImage = breedDao.getImageById(imageId)
                            if (existingImage == null) {
                                try {
                                    val imageResponse = apiService.getImage(imageId)
                                    imageResponse.body()?.let { image ->
                                        breedDao.insertImage(
                                            ImageEntity(
                                                id = imageId,
                                                breedId = breed.id,
                                                url = image.url,
                                                width = image.width,
                                                height = image.height
                                            )
                                        )
                                    }
                                } catch (e: Exception) {
                                    Log.e("BreedRemoteMediator", "Error fetching image $imageId: ${e.message}", e)
                                }
                            }
                        }
                    }
                    .awaitAll()
            }

            val endOfPaginationReached = breeds.isEmpty() || breeds.size < 10

            if (loadType == LoadType.REFRESH) {
                breedDao.clearAll()
            }
            breedDao.insertAll(breeds)

            nextPage = if (endOfPaginationReached) null else page + 1
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            val breedCount = breedDao.getBreedCount()
            if (breedCount > 0) {
                MediatorResult.Success(endOfPaginationReached = false)
            } else {
                MediatorResult.Error(e)
            }
        } catch (e: HttpException) {
            val breedCount = breedDao.getBreedCount()
            if (breedCount > 0) {
                MediatorResult.Success(endOfPaginationReached = false)
            } else {
                MediatorResult.Error(e)
            }
        } catch (e: Exception) {
            val breedCount = breedDao.getBreedCount()
            if (breedCount > 0) {
                MediatorResult.Success(endOfPaginationReached = false)
            } else {
                MediatorResult.Error(e)
            }
        }
    }
}