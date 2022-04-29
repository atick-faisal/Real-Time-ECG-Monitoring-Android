package dev.atick.movesense.repository

import android.bluetooth.BluetoothDevice

interface Movesense {
    fun startScan(onDeviceFound: (BluetoothDevice) -> Unit)
    fun stopScan()
}