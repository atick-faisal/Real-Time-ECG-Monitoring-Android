package dev.atick.storage.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserPreferencesImpl @Inject constructor(
    private val datastore: DataStore<Preferences>
) : UserPreferences {
    companion object {
        const val USER_ID_KEY = "dev.atick.c.zone.user.id"
    }

    override suspend fun saveUserId(userId: Int) {
        datastore.edit { preferences ->
            preferences[stringPreferencesKey(USER_ID_KEY)] = userId.toString()
        }
    }

    override fun getUserId(): Flow<Int> {
        return datastore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[stringPreferencesKey(USER_ID_KEY)]?.toInt() ?: 0
        }
    }
}