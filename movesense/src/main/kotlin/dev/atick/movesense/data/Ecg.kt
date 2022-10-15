package dev.atick.movesense.data

import java.util.*

data class Ecg(
    val signal: List<Int>,
    val rPeaks: List<RPeakData>,
    val timestamp: Long = Date().time
)