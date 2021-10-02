package com.reselling.visionary.data.preferences

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

//private const val TAG = "PreferenceManager"

data class UserInfo(val id: String, val location: String)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext private val context: Context) {


    private val dataStore: DataStore<Preferences> = context.createDataStore(name = "user")


    val preferenceFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
//                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val id = preferences[PreferencesKeys.USER_ID] ?: "-1"
            val loc = preferences[PreferencesKeys.USER_Loc] ?: "0"

            UserInfo(id, loc)
        }


    suspend fun updateUserId(id: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = id
        }
    }

    suspend fun updateUserLoc(loc: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_Loc] = loc
        }
    }

    suspend fun updateUserIdAndLocation(id: String, location: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = id
            preferences[PreferencesKeys.USER_Loc] = location
        }
    }


    suspend fun updateUserPhone(phoneNumber: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_PHONE] = phoneNumber
        }
    }

    val prefFlowPhoneNumber = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
//                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_PHONE]
        }



    private object PreferencesKeys {
        val USER_ID = preferencesKey<String>("user_id")
        val USER_Loc = preferencesKey<String>("user_loc")
        val USER_PHONE = preferencesKey<String>("user_phone")
    }

}