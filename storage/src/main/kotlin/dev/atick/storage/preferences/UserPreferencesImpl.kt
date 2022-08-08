package dev.atick.storage.preferences

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesImpl @Inject constructor(
    private val datastore: DataStore<Preferences>
) : UserPreferences {
    override suspend fun saveUserId(key: String, userId: Int) {
        datastore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = userId.toString()
        }
    }

    override fun getUserId(key: String): Flow<Int> {
        return datastore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[stringPreferencesKey(key)]?.toInt() ?: 0
        }
    }
}