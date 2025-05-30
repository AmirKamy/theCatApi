package com.example.core.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.linker.core.database.dao.BreedWithFavorite
import com.example.linker.core.database.dao.BreedsDao
import com.example.linker.core.database.model.BreedEntity
import com.linker.core.network.LinkerNetworkDataSource

@OptIn(ExperimentalPagingApi::class)
class BreedRemoteMediator(
    private val apiService: LinkerNetworkDataSource,
    private val breedDao: BreedsDao
) : RemoteMediator<Int, BreedWithFavorite>() {
    private var nextPage: Int? = 0 // ذخیره شماره صفحه بعدی

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
            val response = apiService.getBreeds(limit = 10, page = page)
            Log.i("BreedRemoteMediator", "Response code: ${response.code()}, Body size: ${response.body()?.size ?: 0}")

            val breeds = response.body()?.map {
                BreedEntity(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    origin = it.origin,
                    lifeSpan = it.lifeSpan,
                    referenceImageId = it.referenceImageId
                )
            } ?: emptyList()
            Log.i("BreedRemoteMediator", "Mapped breeds: ${breeds.size}")

            if (loadType == LoadType.REFRESH) {
                Log.i("BreedRemoteMediator", "Clearing database")
                breedDao.clearAll()
            }
            breedDao.insertAll(breeds)
            Log.i("BreedRemoteMediator", "Inserted ${breeds.size} breeds into database")

            // افزایش شماره صفحه بعدی فقط اگر داده‌ای دریافت شده باشد
            if (breeds.isNotEmpty()) {
                nextPage = page + 1
            }

            // پایان صفحه‌بندی وقتی تعداد آیتم‌ها کمتر از limit باشد یا پاسخ خالی باشد
            val endOfPaginationReached = breeds.isEmpty() || breeds.size < 10
            Log.i("BreedRemoteMediator", "End of pagination reached: $endOfPaginationReached")
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            Log.e("BreedRemoteMediator", "Error fetching page $page: ${e.message}", e)
            MediatorResult.Error(e)
        }
    }
}