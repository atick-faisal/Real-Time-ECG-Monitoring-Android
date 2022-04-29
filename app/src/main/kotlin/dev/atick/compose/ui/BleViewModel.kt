package dev.atick.compose.ui

import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.core.ui.BaseViewModel
import dev.atick.movesense.repository.Movesense
import javax.inject.Inject

@HiltViewModel
class BleViewModel @Inject constructor(
    private val movesense: Movesense
) : BaseViewModel() {

    val isScanning = mutableStateOf(false)
    val devices = mutableStateListOf<BluetoothDevice>()

    fun toggleScan() {
        if (isScanning.value) {
            isScanning.value = false
            movesense.stopScan()
        } else {
            isScanning.value = true
            movesense.startScan { device ->
                devices.add(device)
            }
        }
    }
}