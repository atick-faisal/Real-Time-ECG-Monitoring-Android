package dev.atick.compose.di.logger

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.LogAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.compose.BuildConfig
import javax.inject.Singleton

@Module(
    includes = [
        FormatStrategyModule::class
    ]
)
@InstallIn(SingletonComponent::class)
object LogAdapterModule {

    @Provides
    @Singleton
    fun provideLogAdapter(formatStrategy: FormatStrategy): LogAdapter {
        return object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        }
    }
}