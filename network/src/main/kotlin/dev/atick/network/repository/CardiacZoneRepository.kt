package dev.atick.network.repository

import dev.atick.network.data.*
import kotlinx.coroutines.flow.StateFlow
import java.io.File

interface CardiacZoneRepository {
    val error: StateFlow<Exception?>
    val abnormalEcg: StateFlow<List<Ecg>>
    suspend fun login(request: LoginRequest): LoginResponse?
    suspend fun pushEcg(request: EcgRequest): PushEcgResponse?
    suspend fun pushAudio(patientId: String, file: File): Boolean
    suspend fun connectDoctor(connectDoctorRequest: ConnectDoctorRequest): Boolean
}