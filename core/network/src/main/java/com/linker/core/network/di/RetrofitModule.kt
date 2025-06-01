package com.linker.core.network.di

import com.linker.core.network.BuildConfig
import com.linker.core.network.retrofit.RetrofitNetwork
import com.linker.core.network.retrofit.RetrofitNetworkApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val LINKER_BASE_URL = "https://api.thecatapi.com/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader(
                        "x-api-key",
                        BuildConfig.API_KEY
                    )
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl(LINKER_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideLinkerNetworkApi(retrofit: Retrofit): RetrofitNetworkApi =
        retrofit.create(RetrofitNetworkApi::class.java)

}




