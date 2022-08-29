package dev.atick.network.api

import dev.atick.network.data.EcgRequest
import dev.atick.network.data.LoginRequest
import dev.atick.network.data.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface CardiacZoneApi {

    companion object {
        const val BASE_URL = "https://stark-lowlands-43915.herokuapp.com"
        // const val BASE_URL = "http://192.168.1.103:5000"
        // const val BASE_URL = "http://18.222.190.174:8080"
    }

    @POST("/user_login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/push_ecg")
    suspend fun pushEcg(@Body ecgRequest: EcgRequest)
}