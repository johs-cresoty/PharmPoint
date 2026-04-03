package com.cresoty.catpospoint.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.cresoty.catpospoint.BuildConfig
import com.cresoty.catpospoint.domain.repository.AppSupportAuthRepository
import com.cresoty.catpospoint.remote.api.AppSupportAuthApi
import com.cresoty.catpospoint.remote.model.request.LoginRequest
import com.cresoty.catpospoint.remote.model.request.LogoutRequest
import com.cresoty.catpospoint.remote.model.request.RefreshTokenRequest
import com.cresoty.catpospoint.remote.model.response.TokenResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class AppSupportAuthRepositoryImpl @Inject constructor(
    private val api: AppSupportAuthApi,
    private val dataStore: DataStore<Preferences>,
) : AppSupportAuthRepository {

    private val tokenKey = stringPreferencesKey("app_support_token")
    private val refreshTokenKey = stringPreferencesKey("app_support_refresh_token")

    // 동시 다발적 토큰 갱신 방지
    private val mutex = Mutex()

    override suspend fun getValidToken(): String = mutex.withLock {
        val stored = dataStore.data.first()[tokenKey]
        if (!stored.isNullOrEmpty()) return@withLock stored
        login()
    }

    override suspend fun forceRefresh(): String = mutex.withLock {
        val storedRefresh = dataStore.data.first()[refreshTokenKey]
        if (!storedRefresh.isNullOrEmpty()) {
            runCatching { api.refreshToken(RefreshTokenRequest(storedRefresh)) }
                .onSuccess { return@withLock saveAndReturn(it) }
        }
        // 리프레시 토큰 없거나 만료 → 재로그인
        login()
    }

    override suspend fun logout() {
        val refreshToken = dataStore.data.first()[refreshTokenKey] ?: return
        runCatching { api.logout(LogoutRequest(refreshToken)) }
        clearTokens()
    }

    private suspend fun login(): String {
        val response = api.login(LoginRequest(BuildConfig.API_ID, BuildConfig.API_PASSWORD))
        return saveAndReturn(response)
    }

    private suspend fun saveAndReturn(response: TokenResponse): String {
        dataStore.edit { prefs ->
            prefs[tokenKey] = response.token
            prefs[refreshTokenKey] = response.refreshToken
        }
        return response.token
    }

    private suspend fun clearTokens() {
        dataStore.edit { prefs ->
            prefs.remove(tokenKey)
            prefs.remove(refreshTokenKey)
        }
    }
}
