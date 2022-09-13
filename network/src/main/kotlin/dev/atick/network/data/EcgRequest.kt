package dev.atick.network.data

import com.google.gson.annotations.SerializedName


data class RPeak(
    @SerializedName("x")
    val x: Int,
    @SerializedName("y")
    val y: Int
)

data class Ecg(
    val id: String = "atick",
    @SerializedName("signal")
    val ecgData: List<Int>,
    @SerializedName("r_peaks")
    val rPeaks: List<RPeak>,
    @SerializedName("v_beats")
    val vBeats: List<Int> = listOf(),
    @SerializedName("s_beats")
    val sBeats: List<Int> = listOf()
)

data class EcgRequest(
    @SerializedName("ecg")
    val ecg: Ecg
)
