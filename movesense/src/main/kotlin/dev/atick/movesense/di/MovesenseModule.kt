package dev.atick.movesense.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.movesense.repository.Movesense
import dev.atick.movesense.repository.MovesenseImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MovesenseModule {

    @Binds
    @Singleton
    abstract fun bindMovesenseModule(
        movesenseImpl: MovesenseImpl
    ): Movesense
}