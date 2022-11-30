package dev.atick.storage.preferences

import kotlinx.coroutines.flow.Flow

interface UserPreferences {
    suspend fun saveUserId(userId: String)
    fun getUserId(): Flow<String>
}