package dev.atick.network.data

import com.google.gson.annotations.SerializedName

data class ConnectDoctorRequest(
    @SerializedName("patient_id")
    val patientId: String,
    @SerializedName("doctor_id")
    val doctorId: String
)
