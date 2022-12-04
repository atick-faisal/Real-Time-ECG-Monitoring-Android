package dev.atick.network.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.network.utils.NetworkUtils
import dev.atick.network.utils.NetworkUtilsImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkUtilsModule {
    @Binds
    @Singleton
    abstract fun bindNetworkUtils(
        networkUtilsImpl: NetworkUtilsImpl
    ): NetworkUtils
}