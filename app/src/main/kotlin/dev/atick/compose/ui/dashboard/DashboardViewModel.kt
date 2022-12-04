package dev.atick.compose.ui.dashboard

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.compose.ui.dashboard.data.DashboardUiState
import dev.atick.compose.ui.dashboard.data.EcgPlotData
import dev.atick.compose.ui.dashboard.data.RecordingState
import dev.atick.compose.ui.dashboard.data.toEcgPlotData
import dev.atick.movesense.Movesense
import dev.atick.movesense.data.EcgSignal
import dev.atick.movesense.data.toCsv
import dev.atick.network.repository.CardiacZoneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    movesense: Movesense,
    cardiacZoneRepository: CardiacZoneRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState>
        get() = _uiState

    private val ecgBuffer = MutableList(300) { 0 }
    private val recording = mutableListOf<EcgSignal>()

    init {
        viewModelScope.launch {
            movesense.heartRate.collect {
                _uiState.update { state -> state.copy(heartRate = it) }
            }
        }

        viewModelScope.launch {
            movesense.ecgSignal.collect { ecgSignal ->
                ecgBuffer.subList(0, ecgSignal.values.size).clear()
                ecgBuffer.addAll(ecgSignal.values)
                _uiState.update { state ->
                    state.copy(ecgPlotData = getEcgPlotData(ecgBuffer))
                }
                updateRecording(ecgSignal)
            }
        }

        viewModelScope.launch {
            cardiacZoneRepository.abnormalEcg.collect { abnormalEcgList ->
                _uiState.update { state ->
                    state.copy(
                        abnormalEcgPlotData = abnormalEcgList.toEcgPlotData()
                    )
                }
            }
        }
    }

    private fun getEcgPlotData(ecgSignal: List<Int>): EcgPlotData {
        return EcgPlotData(
            ecg = LineDataSet(
                ecgSignal.mapIndexed { index, value ->
                    Entry(index.toFloat(), value.toFloat())
                },
                "ECG"
            )
        )
    }

    private fun updateRecording(ecgSignal: EcgSignal) {
        if (uiState.value.recordingState == RecordingState.Recording) {
            recording.add(ecgSignal)
        }
    }

    fun record() {
        if (uiState.value.recordingState == RecordingState.NotRecording) {
            Logger.d("STARTING TO RECORD ... ")
            _uiState.update { state ->
                state.copy(recordingState = RecordingState.Recording)
            }
        } else {
            Logger.d("SAVING DATA ... ")
            saveRecording()
            _uiState.update { state ->
                state.copy(recordingState = RecordingState.NotRecording)
            }
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