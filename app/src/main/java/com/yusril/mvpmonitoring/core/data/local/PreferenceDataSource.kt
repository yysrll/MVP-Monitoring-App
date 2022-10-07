package com.yusril.mvpmonitoring.core.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yusril.mvpmonitoring.core.domain.model.Lecturer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


const val DATASTORE_NAME = "lecturer_datastore"
val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

class PreferenceDataSource @Inject constructor(
    private val context: Context
){

    private val lecturerNameKey = stringPreferencesKey("lecturer_name")
    private val lecturerNIDNKey = stringPreferencesKey("lecturer_nidn")
    private val lecturerTokenKey = stringPreferencesKey("lecturer_token")

    fun getToken(): Flow<String> {
        return context.datastore.data.map {
            it[lecturerTokenKey] ?: ""
        }
    }

    suspend fun setToken(token: String) {
        context.datastore.edit {
            it[lecturerTokenKey] = "Bearer $token"
        }
    }

    fun getCurrentUser(): Flow<Lecturer> {
        return context.datastore.data.map {
            Lecturer(
                name = it[lecturerNameKey] ?: "",
                nidn = it[lecturerNIDNKey] ?: "",
            )
        }
    }

    suspend fun setNewLecturer(lecturer: Lecturer) {
        println("setNewLecturer")
        context.datastore.edit {
            it[lecturerNameKey] = lecturer.name
            it[lecturerNIDNKey] = lecturer.nidn
        }
    }

    suspend fun deleteLecturer() {
        context.datastore.edit {
            it.remove(lecturerNameKey)
            it.remove(lecturerNIDNKey)
            it.remove(lecturerTokenKey)
        }
    }
}