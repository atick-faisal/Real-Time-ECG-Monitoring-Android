package dev.atick.network.repository

import dev.atick.network.data.EcgRequest
import dev.atick.network.data.LoginRequest
import dev.atick.network.data.LoginResponse

interface CardiacZoneRepository {
    suspend fun login(request: LoginRequest): LoginResponse
    suspend fun pushEcg(request: EcgRequest)
}