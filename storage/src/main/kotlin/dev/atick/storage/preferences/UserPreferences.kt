package dev.atick.storage.preferences

import kotlinx.coroutines.flow.Flow

interface UserPreferences {
    suspend fun saveUserId(userId: Int)
    fun getUserId(): Flow<Int>
}