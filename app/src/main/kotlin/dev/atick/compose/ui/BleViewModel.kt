package dev.atick.compose.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.map
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.core.ui.BaseViewModel
import dev.atick.movesense.data.BtDevice
import dev.atick.movesense.repository.Movesense
import dev.atick.movesense.service.MovesenseService
import javax.inject.Inject

@HiltViewModel
class BleViewModel @Inject constructor(
    private val movesense: Movesense
) : BaseViewModel() {

    companion object {
        const val N_DATA_POINTS = 300
    }

    val isConnected = movesense.isConnected
    val connectionStatus = movesense.connectionStatus
    val averageHeartRate = movesense.averageHeartRate
    val rrInterval = movesense.rrInterval
    var ecgDataset = movesense.ecgData.map { ecgBuffer ->
        LineDataSet(
            ecgBuffer.subList(
                ecgBuffer.size - N_DATA_POINTS,
                ecgBuffer.size
            ).mapIndexed { index, value ->
                Entry(index.toFloat(), value.toFloat())
            },
            "ECG"
        )
    }

    val isScanning = mutableStateOf(false)
    val devices = mutableStateListOf<BtDevice>()

    fun startScan() {
        if (!isScanning.value) {
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

    fun stopScan() {
        if (isScanning.value) {
            isScanning.value = false
            movesense.stopScan()
        }
    }

    fun disconnect() {
        movesense.clear()
        // TODO(IMPLEMENT THIS)
    }

    override fun onCleared() {
        if (!MovesenseService.STARTED) {
            movesense.clear()
        }
        super.onCleared()
    }
}