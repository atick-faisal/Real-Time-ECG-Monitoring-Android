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
    val id: Long = Date().time,
    @SerializedName("signal")
    val ecgData: List<Int>,
    @SerializedName("r_peaks")
    val rPeaks: List<RPeak>,
    @SerializedName("v_beats")
    val vBeats: List<RPeak> = listOf(),
    @SerializedName("s_beats")
    val sBeats: List<RPeak> = listOf()
)