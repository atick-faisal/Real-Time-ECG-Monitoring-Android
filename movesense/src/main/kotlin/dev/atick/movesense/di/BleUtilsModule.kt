package dev.atick.movesense.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.movesense.utils.BleUtils
import dev.atick.movesense.utils.BleUtilsImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class BleUtilsModule {
    @Binds
    @Singleton
    abstract fun bindBleUtils(
        bleUtilsImpl: BleUtilsImpl
    ): BleUtils
}