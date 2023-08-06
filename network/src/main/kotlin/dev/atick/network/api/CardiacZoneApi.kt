package dev.atick.network.api

import dev.atick.network.data.*
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CardiacZoneApi {

    companion object {
        const val BASE_URL = "http://34.133.239.54"
    }

    @POST("/patient_login")
    suspend fun login(@Body request: LoginRequest): LoginResponse?

    @POST("/push_ecg")
    suspend fun pushEcg(@Body ecgRequest: EcgRequest): PushEcgResponse?

    @Multipart
    @POST("/push_patient_audio/{patient_id}")
    suspend fun pushAudio(
        @Path("patient_id") patientId: String,
        @Part audio: MultipartBody.Part
    ): PushAudioResponse

    @POST("/connect_doctor")
    suspend fun connectDoctor(@Body connectDoctorRequest: ConnectDoctorRequest): ConnectDoctorResponse?
}