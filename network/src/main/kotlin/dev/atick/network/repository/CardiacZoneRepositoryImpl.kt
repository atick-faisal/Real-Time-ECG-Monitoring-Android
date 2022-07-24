package dev.atick.network.repository

import dev.atick.network.api.CardiacZoneApi
import dev.atick.network.data.Request
import javax.inject.Inject

class CardiacZoneRepositoryImpl @Inject constructor(
    private val cardiacZoneAPi: CardiacZoneApi
) : CardiacZoneRepository {
    override suspend fun pushEcg(request: Request) {
        cardiacZoneAPi.pushEcg(request)
    }
}