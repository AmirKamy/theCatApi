package com.linker.core.network.retrofit

import Breed
import com.linker.core.network.LinkerNetworkDataSource
import com.linker.core.network.model.ImageDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

interface RetrofitNetworkApi {

    @GET("v1/breeds")
    suspend fun getItems(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Response<List<Breed>>

    @GET("v1/images/{imageId}")
    suspend fun getImage(
        @Path("imageId") imageId: String
    ): Response<ImageDto>

}


@Singleton
class RetrofitNetwork @Inject constructor(
    private val api: RetrofitNetworkApi
) : LinkerNetworkDataSource {
    override suspend fun getBreeds(page: Int, limit: Int): Response<List<Breed>> =
        api.getItems(page, limit)

    override suspend fun getImage(imageId: String): Response<ImageDto> =
        api.getImage(imageId)


}
