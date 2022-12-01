package dev.atick.compose.ui

import android.os.Environment
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterDataSet
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.compose.ui.home.data.toEcgPlotData
import dev.atick.core.ui.BaseViewModel
import dev.atick.core.utils.Event
import dev.atick.movesense.data.BtDevice
import dev.atick.movesense.data.EcgSignal
import dev.atick.movesense.data.toCsv
import dev.atick.movesense.repository.Movesense
import dev.atick.movesense.service.MovesenseService
import dev.atick.network.data.ConnectDoctorRequest
import dev.atick.network.repository.CardiacZoneRepository
import dev.atick.storage.preferences.UserPreferences
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BleViewModel @Inject constructor(
    private val movesense: Movesense,
    private val cardiacZoneRepository: CardiacZoneRepository,
    private val userPreferences: UserPreferences
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
    val abnormalEcgList = cardiacZoneRepository.abnormalEcg.map { x -> x.toEcgPlotData() }

    private val _connectDoctorStatus = MutableLiveData<Event<String>>()
    val connectDoctorStatus: LiveData<Event<String>>
        get() = _connectDoctorStatus

    sealed class RecordState(val description: String) {
        object Recording : RecordState("STOP RECORDING")
        object NotRecording : RecordState("RECORD")
    }

    private var patientId = "-1"

    init {
        viewModelScope.launch {
            userPreferences.getUserId().collect { id ->
                Logger.w("USER ID: $id")
                patientId = id
            }
        }
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

    fun connectDoctor(doctorId: String) {
        viewModelScope.launch {
            val success = cardiacZoneRepository.connectDoctor(
                ConnectDoctorRequest(
                    patientId = patientId,
                    doctorId = doctorId
                )
            )
            if (success) {
                _connectDoctorStatus.postValue(Event("Doctor Added Successfully"))
            } else {
                _connectDoctorStatus.postValue(Event("Error Adding Doctor"))
            }
        }
    }


}

