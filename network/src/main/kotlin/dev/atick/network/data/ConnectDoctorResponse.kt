package dev.atick.network.data

import com.google.gson.annotations.SerializedName

data class ConnectDoctorResponse(
    @SerializedName("success")
    val success: Boolean
)
