package dev.atick.network.data

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("patient_id")
    val patientId: Int,

    @SerializedName("patient_name")
    val patientName: String
)
