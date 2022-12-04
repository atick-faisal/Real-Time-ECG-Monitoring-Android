package dev.atick.compose.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.compose.ui.dashboard.data.DashboardUiState
import dev.atick.compose.ui.home.data.EcgPlotData
import dev.atick.compose.ui.home.data.toEcgPlotData
import dev.atick.movesense.Movesense
import dev.atick.network.repository.CardiacZoneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    init {
        viewModelScope.launch {
            movesense.connectionState.collect {
                _uiState.update { state -> state.copy(connectionState = it) }
            }
        }

        viewModelScope.launch {
            movesense.heartRate.collect {
                _uiState.update { state -> state.copy(heartRate = it) }
            }
        }

        viewModelScope.launch {
            movesense.ecgSignal.collect { ecg ->
                ecgBuffer.subList(0, ecg.size).clear()
                ecgBuffer.addAll(ecg)
                _uiState.update { state ->
                    state.copy(ecgPlotData = getEcgPlotData(ecgBuffer))
                }
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
}