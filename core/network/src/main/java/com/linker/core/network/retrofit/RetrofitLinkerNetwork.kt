//package com.linker.core.network.retrofit
//
//import com.linker.core.network.LinkerNetworkDataSource
//import com.linker.core.network.model.LoginResponse
//import com.example.linker.core.model.LoginToServer
//import com.linker.core.network.model.ProductDto
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.Body
//import retrofit2.http.GET
//import retrofit2.http.Path
//import javax.inject.Inject
//import javax.inject.Singleton
//
//interface RetrofitLinkerNetworkApi {
//
//    @GET(value = "auth/login")
//    suspend fun login(
//        @Body loginToServer: LoginToServer
//    ): LoginResponse
//
//    @GET(value = "products")
//    suspend fun getAllProducts(): List<ProductDto>
//
//    @GET("products/{id}")
//    suspend fun getProductById(@Path("id") id: Int): ProductDto
//
//}
//
//private const val LINKER_BASE_URL = "https://fakestoreapi/"
//
//@Singleton
//class RetrofitLinkerNetwork @Inject constructor(
//    private val api: RetrofitLinkerNetworkApi
//) : LinkerNetworkDataSource {
//
//    override suspend fun login(loginToServer: LoginToServer): LoginResponse =
//        api.login(loginToServer)
//
//    override suspend fun getAllProducts(): List<ProductDto> =
//        api.getAllProducts()
//
//    override suspend fun getProductById(id: Int): ProductDto =
//        api.getProductById(id)
//
//}
