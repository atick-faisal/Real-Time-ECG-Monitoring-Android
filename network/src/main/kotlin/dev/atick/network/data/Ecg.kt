package dev.atick.network.data

import com.google.gson.annotations.SerializedName
import java.util.*

data class RPeak(
    @SerializedName("x")
    val x: Int,
    @SerializedName("y")
    val y: Int
)

data class Ecg(
    @SerializedName("id")
    val id: Long,
    @SerializedName("signal")
    val ecgData: List<Int>,
    @SerializedName("r_peaks")
    val rPeaks: List<RPeak> = listOf(),
    @SerializedName("v_beats")
    val vBeats: List<RPeak> = listOf(),
    @SerializedName("s_beats")
    val sBeats: List<RPeak> = listOf(),
    @SerializedName("af")
    val af: Int = 1,
    @SerializedName("heart_rate")
    val heartRate: Float = 80.0F,
    @SerializedName("rr_intervals")
    val rrIntervals: List<Int> = listOf()
)