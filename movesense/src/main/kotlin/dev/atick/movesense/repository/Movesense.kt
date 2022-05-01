package dev.atick.movesense.repository

import androidx.lifecycle.LiveData
import dev.atick.core.utils.Event
import dev.atick.movesense.data.BtDevice

interface Movesense {
    val isConnected: LiveData<Boolean>
    val connectionStatus: LiveData<Event<String?>>
    val averageHeartRate: LiveData<Float>
    val rrInterval: LiveData<Int>
    val ecgData: LiveData<List<Int>>
    fun startScan(onDeviceFound: (BtDevice) -> Unit)
    fun connect(address: String, onConnect: () -> Unit)
    fun stopScan()
    fun clear()
}