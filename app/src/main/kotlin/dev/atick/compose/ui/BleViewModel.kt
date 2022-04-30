package dev.atick.compose.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.core.ui.BaseViewModel
import dev.atick.movesense.data.BtDevice
import dev.atick.movesense.repository.Movesense
import javax.inject.Inject

@HiltViewModel
class BleViewModel @Inject constructor(
    private val movesense: Movesense
) : BaseViewModel() {

    val connectionStatus = movesense.connectionStatus
    val averageHeartRate = movesense.averageHeartRate
    val isScanning = mutableStateOf(false)
    val devices = mutableStateListOf<BtDevice>()

    fun toggleScan() {
        if (isScanning.value) {
            isScanning.value = false
            movesense.stopScan()
        } else {
            isScanning.value = true
            movesense.startScan { btDevice ->
                // ... Required for RSSI update
                val indexQuery = devices.indexOfFirst { device ->
                    device.address == btDevice.address
                }
                if (indexQuery != -1) {
                    devices[indexQuery] = btDevice
                } else {
                    devices.add(btDevice)
                }
            }
        }
    }

    fun connect(address: String) {
        movesense.connect(address) {

        }
    }
}