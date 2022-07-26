package dev.atick.network.repository

import com.orhanobut.logger.Logger
import dev.atick.network.api.CardiacZoneApi
import dev.atick.network.data.Request
import javax.inject.Inject

class CardiacZoneRepositoryImpl @Inject constructor(
    private val cardiacZoneAPi: CardiacZoneApi
) : CardiacZoneRepository {
    override suspend fun pushEcg(request: Request) {
        try {
            cardiacZoneAPi.pushEcg(request)
        } catch (e: Exception) {
            Logger.e("ECG DATA NOT SENT! ${e.message}")
        }
    }
}