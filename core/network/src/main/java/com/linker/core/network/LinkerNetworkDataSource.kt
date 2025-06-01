package com.linker.core.network

import Breed
import com.linker.core.network.model.BreedDto
import com.linker.core.network.model.ImageDto
import retrofit2.Response

interface LinkerNetworkDataSource {

    suspend fun getBreeds(
        page: Int,
        limit: Int
    ): Response<List<Breed>>

    suspend fun getImage(imageId: String): Response<ImageDto>

    suspend fun getBreedDetailImages(
        page: Int,
        limit: Int,
        breedId: String
    ): Response<List<ImageDto>>

    suspend fun getBreedDetail(breedId: String): BreedDto

    suspend fun searchBread(query: String): Response<List<Breed>>

}