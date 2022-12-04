package dev.atick.compose.ui.dashboard.data

import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterDataSet

data class EcgPlotData(
    val ecgSignal: LineDataSet,
    val rPeaks: ScatterDataSet = ScatterDataSet(listOf(), "R-PEAK"),
    val vBeats: ScatterDataSet = ScatterDataSet(listOf(), "V-BEAT"),
    val sBeats: ScatterDataSet = ScatterDataSet(listOf(), "S-BEAT"),
)
