package com.cresoty.catpospoint.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.cresoty.catpospoint.domain.repository.FcmRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FcmRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : FcmRepository {

    private val fcmTokenKey = stringPreferencesKey("fcm_token")

    override suspend fun saveToken(token: String) {
        dataStore.edit { it[fcmTokenKey] = token }
    }

    override suspend fun getToken(): String? {
        return dataStore.data.first()[fcmTokenKey]
    }
}
