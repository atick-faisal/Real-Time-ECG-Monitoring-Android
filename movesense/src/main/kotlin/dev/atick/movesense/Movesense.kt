package dev.atick.movesense

import dev.atick.movesense.data.ConnectionState
import dev.atick.movesense.data.EcgSignal
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface Movesense {
    val heartRate: SharedFlow<Float>
    val ecgSignal: SharedFlow<EcgSignal>
    val connectionState: StateFlow<ConnectionState>

    suspend fun getMovesenseDeviceAddress(): String?
    fun initiateConnection(address: String)
    fun freeResources()
}