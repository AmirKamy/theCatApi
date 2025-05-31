package com.example.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.core.data.ImageRemoteMediator
import com.example.linker.core.database.dao.BreedDetailImageDao
import com.example.linker.core.database.model.toModel
import com.example.linker.core.model.Image
import com.linker.core.network.LinkerNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class BreedsDetailRepositoryImpl @Inject constructor(
    private val apiService: LinkerNetworkDataSource,
    private val breedDao: BreedDetailImageDao
) : BreedsDetailRepository {
    override fun getBreedDetailImages(breedId: String): Flow<PagingData<Image>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false,
                initialLoadSize = 5,
                prefetchDistance = 1
            ),
            remoteMediator = ImageRemoteMediator(apiService, breedDao, breedId),
            pagingSourceFactory = { breedDao.getDetailImages(breedId) }
        ).flow.map { pagingData ->
            pagingData.map { it.toModel() }
        }
    }

}