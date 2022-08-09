package dev.atick.network.repository

import com.orhanobut.logger.Logger
import dev.atick.network.api.CardiacZoneApi
import dev.atick.network.data.EcgRequest
import dev.atick.network.data.LoginRequest
import dev.atick.network.data.LoginResponse
import javax.inject.Inject

class CardiacZoneRepositoryImpl @Inject constructor(
    private val cardiacZoneAPi: CardiacZoneApi
) : CardiacZoneRepository {
    override suspend fun login(request: LoginRequest): LoginResponse {
        return try {
            cardiacZoneAPi.login(request)
        } catch (e: Exception) {
            Logger.e("LOGIN ATTEMPT UNSUCCESSFUL! ${e.message}")
            LoginResponse(
                success = false,
                patientId = 0,
                patientName = "Unknown"
            )
        }
    }

    override suspend fun pushEcg(request: EcgRequest) {
        try {
            cardiacZoneAPi.pushEcg(request)
        } catch (e: Exception) {
            Logger.e("ECG DATA NOT SENT! ${e.message}")
        }
    }
}