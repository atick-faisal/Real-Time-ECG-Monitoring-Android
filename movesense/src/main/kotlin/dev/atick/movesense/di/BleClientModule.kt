package dev.atick.movesense.di

import android.content.Context
import com.orhanobut.logger.Logger
import com.polidea.rxandroidble2.BuildConfig
import com.polidea.rxandroidble2.LogConstants
import com.polidea.rxandroidble2.LogOptions
import com.polidea.rxandroidble2.RxBleClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BleClientModule {

    @Provides
    @Singleton
    fun provideRxBleClient(
        @ApplicationContext appContext: Context
    ): RxBleClient? {
        RxBleClient.updateLogOptions(
            LogOptions.Builder()
                .setLogLevel(
                    if (BuildConfig.DEBUG) LogConstants.DEBUG
                    else LogConstants.NONE
                )
                .setLogger { level, tag, message ->
                    Logger.log(level, tag, message, null)
                }
                .build()
        )
        return RxBleClient.create(appContext)
    }
}