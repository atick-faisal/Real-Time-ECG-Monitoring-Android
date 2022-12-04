package dev.atick.network.data

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @SerializedName("patient")
    val patient: Patient

)

data class Patient(
    @SerializedName("patient_id")
    val patientId: String,

    @SerializedName("name")
    val patientName: String
)