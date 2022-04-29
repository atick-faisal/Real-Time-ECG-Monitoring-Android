package dev.atick.movesense.di

import android.content.Context
import com.movesense.mds.Mds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MdsModule {

    @Provides
    @Singleton
    fun provideMds(
        @ApplicationContext appContext: Context
    ): Mds? {
        return Mds.builder().build(appContext)
    }
}