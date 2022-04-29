package dev.atick.movesense.repository

import com.polidea.rxandroidble2.RxBleDevice

interface Movesense {
    fun startScan(onDeviceFound: (RxBleDevice) -> Unit)
    fun stopScan()
}