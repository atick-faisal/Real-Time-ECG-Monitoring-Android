package dev.atick.network.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.network.repository.CardiacZoneRepository
import dev.atick.network.repository.CardiacZoneRepositoryImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCardiacZoneRepository(
        glucoseRepositoryImpl: CardiacZoneRepositoryImpl
    ): CardiacZoneRepository
}