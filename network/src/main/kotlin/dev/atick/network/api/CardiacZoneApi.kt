package dev.atick.network.api

import dev.atick.network.data.Request
import retrofit2.http.Body
import retrofit2.http.POST

interface CardiacZoneApi {
    @POST("/push_ecg")
    suspend fun pushEcg(@Body request: Request)
}