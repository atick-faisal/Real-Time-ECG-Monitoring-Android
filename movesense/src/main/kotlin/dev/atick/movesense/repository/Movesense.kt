package dev.atick.movesense.repository

import dev.atick.movesense.data.BtDevice

interface Movesense {
    fun startScan(onDeviceFound: (BtDevice) -> Unit)
    fun stopScan()
    fun connect(address: String, onConnect: () -> Unit)
}