package dev.atick.movesense.data

import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("AvailableSampleRates")
    val availableSampleRates: List<Int>,
    @SerializedName("CurrentSampleRate")
    val currentSampleRate: Int,
    @SerializedName("ArraySize")
    val arraySize: Int
)
