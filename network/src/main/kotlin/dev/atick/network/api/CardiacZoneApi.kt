package dev.atick.network.api

import dev.atick.network.data.EcgRequest
import dev.atick.network.data.LoginRequest
import dev.atick.network.data.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface CardiacZoneApi {
    @POST("/user_login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/push_ecg")
    suspend fun pushEcg(@Body request: EcgRequest)
}