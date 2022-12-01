package dev.atick.network.data

import com.google.gson.annotations.SerializedName
import java.util.*


data class EcgRequest(
    @SerializedName("patient_id")
    val patientId: String,
    @SerializedName("ecg")
    val ecg: Ecg
)
