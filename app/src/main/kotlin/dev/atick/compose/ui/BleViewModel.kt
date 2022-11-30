package dev.atick.compose.ui

import android.os.Environment
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.map
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterDataSet
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.core.ui.BaseViewModel
import dev.atick.movesense.data.BtDevice
import dev.atick.movesense.data.EcgSignal
import dev.atick.movesense.data.toCsv
import dev.atick.movesense.repository.Movesense
import dev.atick.movesense.service.MovesenseService
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BleViewModel @Inject constructor(
    private val movesense: Movesense
) : BaseViewModel() {

    val isConnected = movesense.isConnected
    val connectionStatus = movesense.connectionStatus
    val averageHeartRate = movesense.averageHeartRate
    val rrInterval = movesense.rrInterval
    var ecgDataset = movesense.ecgData.map { ecgBuffer ->
        LineDataSet(
//            ecgBuffer.subList(
//                ecgBuffer.size - N_DATA_POINTS,
//                ecgBuffer.size
//            )
            ecgBuffer.mapIndexed { index, value ->
                Entry(index.toFloat(), value.toFloat())
            },
            "ECG"
        )
    }

    var rPeakDataset = movesense.rPeakData.map { rPeakData ->
        ScatterDataSet(
            rPeakData.map { rPeak ->
                Entry(
                    rPeak.location.toFloat(),
                    rPeak.amplitude.toFloat()
                )
            },
            "R-PEAK"
        )
    }

    val ecgSignal = movesense.ecgSignal

    val isScanning = mutableStateOf(false)
    val devices = mutableStateListOf<BtDevice>()

    val recordState = mutableStateOf<RecordState>(RecordState.NotRecording)
    private val recording = mutableListOf<EcgSignal>()

    sealed class RecordState(val description: String) {
        object Recording : RecordState("STOP RECORDING")
        object NotRecording : RecordState("RECORD")
    }

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

    fun updateRecording(ecgSignal: EcgSignal) {
        if (recordState.value == RecordState.Recording) {
            recording.add(ecgSignal)
        }
    }

    fun record() {
        if (recordState.value == RecordState.NotRecording) {
            Logger.d("STARTING TO RECORD ... ")
            recordState.value = RecordState.Recording
        } else {
            Logger.d("SAVING DATA ... ")
            saveRecording()
            recordState.value = RecordState.NotRecording
            recording.clear()
        }
    }

    private fun saveRecording() {
        val timestamp = SimpleDateFormat("dd_M_yyyy_hh_mm_ss", Locale.US).format(Date())
        val savePath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        Files.createDirectories(savePath.toPath())
        val csvData = recording.toCsv()

        val myExternalFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "movesense_${timestamp}.csv"
        )

        try {
            val fos = FileOutputStream(myExternalFile)
            fos.write(csvData.toByteArray())
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}