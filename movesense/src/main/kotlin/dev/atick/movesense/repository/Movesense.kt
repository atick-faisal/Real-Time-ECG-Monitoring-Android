package dev.atick.movesense.repository

import androidx.lifecycle.LiveData
import dev.atick.movesense.data.BtDevice
import dev.atick.movesense.data.ConnectionStatus
import dev.atick.movesense.data.Ecg
import dev.atick.movesense.data.RPeakData

interface Movesense {
    val isConnected: LiveData<Boolean>
    val connectionStatus: LiveData<ConnectionStatus>
    val averageHeartRate: LiveData<Float>
    val rrInterval: LiveData<Int>
    val ecgData: LiveData<List<Int>>
    val rPeakData: LiveData<List<RPeakData>>
    val ecg: LiveData<Ecg>
    fun startScan(onDeviceFound: (BtDevice) -> Unit)
    fun connect(address: String, onConnect: () -> Unit)
    fun stopScan()
    fun clear()
}