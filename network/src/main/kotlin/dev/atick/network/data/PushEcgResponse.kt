package dev.atick.network.data

import com.google.gson.annotations.SerializedName

data class PushEcgResponse(
    @SerializedName("ecg")
    val ecg: Ecg
)
