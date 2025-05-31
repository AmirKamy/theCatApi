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
                Log.i("BreedRemoteMediator", "Refreshing data, resetting to page: 0")
                nextPage = 0
                0
            }
            LoadType.PREPEND -> {
                Log.i("BreedRemoteMediator", "Prepend requested, stopping")
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val currentPage = nextPage ?: return MediatorResult.Success(endOfPaginationReached = true)
                Log.i("BreedRemoteMediator", "Appending, requesting page: $currentPage")
                currentPage
            }
        }

        return try {
            Log.i("BreedRemoteMediator", "Fetching page: $page with limit: 10")
 //           delay(5000)
            val response = apiService.getBreeds(limit = 10, page = page)

          //  Log.i("BreedRemoteMediator", "Response code: ${response.code()}, Body size: ${response.body()?.size ?: 0}")
            for (breed in response.body() ?: emptyList()) {
                Log.i("data1", "Fetched breed: ${breed.id}")
            }
            val breeds = response.body()?.map {
                BreedEntity(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    origin = it.origin,
                    lifeSpan = it.life_span,
                    referenceImageId = it.reference_image_id
                )
            } ?: emptyList()
            Log.i("BreedRemoteMediator", "Mapped breeds: ${breeds.size}")

            // دریافت تصاویر به‌صورت موازی برای breedهایی که referenceImageId دارن
            withContext(Dispatchers.IO) {
                Log.i("BreedRemoteMediator", "before async")
                Log.i("BreedRemoteMediator", "referenceImageIds: ${breeds.toString()}")
                breeds.filter { it.referenceImageId != null }
                    .map { breed ->

                        async {
                            val imageId = breed.referenceImageId!!
                            val existingImage = breedDao.getImageById(imageId)
                            Log.i("BreedRemoteMediator", "Existing image: $existingImage")
                            if (existingImage == null) {
                                try {
                                    Log.i("BreedRemoteMediator", "Fetching image for referenceImageId: $imageId")
                                    val imageResponse = apiService.getImage(imageId)
                                    Log.i("BreedRemoteMediator", "Fetched image for referenceImageId: ${imageResponse.body()?.url}")
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
                                        Log.i("BreedRemoteMediator", "Inserted image for breedId: ${breed.id}")
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
                Log.i("BreedRemoteMediator", "Clearing database")
                breedDao.clearAll()
            }
            breedDao.insertAll(breeds)
            Log.i("BreedRemoteMediator", "Inserted ${breeds.size} breeds into database")

            if (endOfPaginationReached) {
                nextPage = null
            } else {
                nextPage = page + 1
            }
            Log.i("BreedRemoteMediator", "End of pagination reached: $endOfPaginationReached")
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            Log.e("BreedRemoteMediator", "Error fetching page $page: ${e.message}", e)
            val breedCount = breedDao.getBreedCount()
            Log.i("BreedRemoteMediator", "Offline mode, breeds in database: $breedCount")
            if (breedCount > 0) {
                return MediatorResult.Success(endOfPaginationReached = false)
            }
            MediatorResult.Error(e)
        }
    }
}