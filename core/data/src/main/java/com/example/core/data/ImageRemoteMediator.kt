package com.example.core.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.linker.core.database.dao.BreedDetailImageDao
import com.example.linker.core.database.dao.BreedsDao
import com.example.linker.core.database.model.DetailImageEntity
import com.linker.core.network.LinkerNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class ImageRemoteMediator(
    private val apiService: LinkerNetworkDataSource,
    private val breedDetailImageDao: BreedDetailImageDao,
    private val breedId: String
) : RemoteMediator<Int, DetailImageEntity>() {
    private var nextPage: Int? = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DetailImageEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                Log.i("ImageRemoteMediator", "Refreshing images for breedId: $breedId, resetting to page: 0")
                nextPage = 0
                0
            }
            LoadType.PREPEND -> {
                Log.i("ImageRemoteMediator", "Prepend requested, stopping")
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val currentPage = nextPage ?: return MediatorResult.Success(endOfPaginationReached = true)
                Log.i("ImageRemoteMediator", "Appending, requesting page: $currentPage")
                currentPage
            }
        }

        return try {
            Log.i("ImageRemoteMediator", "Fetching images for breedId: $breedId, page: $page, limit: 10")
            val response = apiService.getBreedDetailImages(breedId = breedId, limit = 5, page = page)
            Log.i("ImageRemoteMediator", "Response code: ${response.code()}, ${response.body().toString()}")

            val images = response.body()?.map {
                DetailImageEntity(
                    imageId = it.id,
                    breedId = breedId,
                    url = it.url,
                    width = it.width,
                    height = it.height
                )
            } ?: emptyList()
            Log.i("ImageRemoteMediator", "Mapped images: ${images.size}")

            withContext(Dispatchers.IO) {
                if (loadType == LoadType.REFRESH) {
                    Log.i("ImageRemoteMediator", "Clearing detail images for breedId: $breedId")
                    breedDetailImageDao.clearDetailImages(breedId)
                }
                breedDetailImageDao.insertDetailImages(images)
                Log.i("ImageRemoteMediator", "Inserted ${images.size} detail images for breedId: $breedId")
            }

            val endOfPaginationReached = images.isEmpty() || images.size < 5
            if (endOfPaginationReached) {
                nextPage = null
                Log.i("ImageRemoteMediator", "End of pagination reached: no more data")
            } else {
                nextPage = page + 1
                Log.i("ImageRemoteMediator", "Next page set to: $nextPage")
            }

            Log.i("ImageRemoteMediator", "End of pagination reached: $endOfPaginationReached")
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            Log.e("ImageRemoteMediator", "Error fetching images for breedId: $breedId, page: $page: ${e.message}", e)
            val imageCount = withContext(Dispatchers.IO) {
                breedDetailImageDao.getDetailImageCount(breedId)
            }
            Log.i("ImageRemoteMediator", "Offline mode, images in database: $imageCount")
            if (imageCount > 0) {
                return MediatorResult.Success(endOfPaginationReached = false)
            }
            MediatorResult.Error(e)
        }
    }
}