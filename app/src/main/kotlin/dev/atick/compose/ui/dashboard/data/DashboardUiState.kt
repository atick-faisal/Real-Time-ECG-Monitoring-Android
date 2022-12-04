package dev.atick.compose.ui.dashboard.data

import dev.atick.compose.ui.home.data.EcgPlotData
import dev.atick.movesense.data.ConnectionState

data class DashboardUiState(
    val heartRate: Float = 0.0F,
    val ecgPlotData: EcgPlotData = EcgPlotData(),
    val abnormalEcgPlotData: List<EcgPlotData> = listOf(),
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED
)
