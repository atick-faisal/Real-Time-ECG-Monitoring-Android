package dev.atick.network.repository

import dev.atick.network.data.Request

interface CardiacZoneRepository {
    suspend fun pushEcg(request: Request)
}