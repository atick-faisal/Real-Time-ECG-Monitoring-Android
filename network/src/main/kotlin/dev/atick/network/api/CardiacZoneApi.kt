package dev.atick.network.api

import dev.atick.network.data.*
import retrofit2.http.Body
import retrofit2.http.POST

interface CardiacZoneApi {

    companion object {
        const val BASE_URL = "http://34.133.239.54"
    }

    @POST("/patient_login")
    suspend fun login(@Body request: LoginRequest): LoginResponse?

    @POST("/push_ecg")
    suspend fun pushEcg(@Body ecgRequest: EcgRequest): PushEcgResponse?

    @POST("/connect_doctor")
    suspend fun connectDoctor(@Body connectDoctorRequest: ConnectDoctorRequest): ConnectDoctorResponse?
}