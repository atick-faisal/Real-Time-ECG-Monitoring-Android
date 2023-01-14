package dev.atick.network.repository

import dev.atick.network.data.*
import kotlinx.coroutines.flow.StateFlow

interface CardiacZoneRepository {
    val error: StateFlow<String>
    val abnormalEcg: StateFlow<List<Ecg>>
    suspend fun login(request: LoginRequest): LoginResponse?
    suspend fun pushEcg(request: EcgRequest): PushEcgResponse?
    suspend fun connectDoctor(connectDoctorRequest: ConnectDoctorRequest): Boolean
}