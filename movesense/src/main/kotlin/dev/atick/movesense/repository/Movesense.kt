package dev.atick.movesense.repository

import androidx.lifecycle.LiveData
import dev.atick.movesense.data.BtDevice
import dev.atick.movesense.data.ConnectionStatus

interface Movesense {
    val isConnected: LiveData<Boolean>
    val connectionStatus: LiveData<ConnectionStatus>
    val averageHeartRate: LiveData<Float>
    val rrInterval: LiveData<Int>
    val ecgData: LiveData<List<Int>>
    fun startScan(onDeviceFound: (BtDevice) -> Unit)
    fun connect(address: String, onConnect: () -> Unit)
    fun stopScan()
    fun clear()
}