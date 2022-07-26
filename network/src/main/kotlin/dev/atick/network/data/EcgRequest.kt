package dev.atick.network.data

import com.google.gson.annotations.SerializedName

data class EcgRequest(
    @SerializedName("EcgData")
    val ecgData: List<Int>,
    @SerializedName("Time")
    val time: List<String>,
    @SerializedName("UserId")
    val userId: Int
)
