package dev.atick.movesense.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BlehAdapterModule {

    @Provides
    @Singleton
    fun provideBluetoothAdapter(
        @ApplicationContext appContext: Context
    ): BluetoothAdapter? {
        val bluetoothManager = appContext
            .getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return bluetoothManager.adapter
    }
}