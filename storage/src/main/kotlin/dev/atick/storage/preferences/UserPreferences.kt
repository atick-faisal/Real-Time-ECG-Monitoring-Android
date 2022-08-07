package dev.atick.storage.preferences

import kotlinx.coroutines.flow.Flow

interface UserPreferences {
    suspend fun saveUserId(key: String, userId: Int)
    fun getUserId(key: String): Flow<Int>
}