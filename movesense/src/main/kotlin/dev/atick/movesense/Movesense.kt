package dev.atick.movesense

import dev.atick.movesense.data.ConnectionState
import kotlinx.coroutines.flow.SharedFlow

interface Movesense {
    val heartRate: SharedFlow<Float>
    val ecgSignal: SharedFlow<List<Int>>
    val connectionState: SharedFlow<ConnectionState>

    suspend fun getMovesenseDeviceAddress(): String?
    fun initiateConnection(address: String)
    fun freeResources()
}