package dev.atick.movesense.repository

import androidx.lifecycle.LiveData
import dev.atick.core.utils.Event
import dev.atick.movesense.data.BtDevice

interface Movesense {
    val connectionStatus: LiveData<Event<String?>>
    val averageHeartRate: LiveData<Float>
    fun startScan(onDeviceFound: (BtDevice) -> Unit)
    fun connect(address: String, onConnect: () -> Unit)
    fun stopScan()
}