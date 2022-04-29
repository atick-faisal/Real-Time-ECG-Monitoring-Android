package dev.atick.movesense.repository

import android.bluetooth.BluetoothDevice
import dev.atick.movesense.data.BtDevice

interface Movesense {
    fun startScan(onDeviceFound: (BtDevice) -> Unit)
    fun stopScan()
}