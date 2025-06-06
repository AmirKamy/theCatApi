package com.linker.core.network.di

import com.linker.core.network.LinkerNetworkDataSource
import com.linker.core.network.retrofit.RetrofitNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindLinkerNetworkDataSource(
        impl: RetrofitNetwork
    ): LinkerNetworkDataSource
}