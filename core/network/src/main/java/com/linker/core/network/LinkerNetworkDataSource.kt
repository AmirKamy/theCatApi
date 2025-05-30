package com.linker.core.network

import Breed
import retrofit2.Response

interface LinkerNetworkDataSource {

    suspend fun getBreeds(
        page: Int,
        limit: Int
    ): Response<List<Breed>>

}