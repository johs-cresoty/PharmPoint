package com.cresoty.catpospoint.data.repository.impl

import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.cresoty.catpospoint.data.repository.ConfigKey
import com.cresoty.catpospoint.data.repository.ConfigRepository
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
    private val configRepository: ConfigRepository,
) : AppSupportAuthRepository {

    private val tokenKey = stringPreferencesKey("app_support_token")
    private val refreshTokenKey = stringPreferencesKey("app_support_refresh_token")

    // 동시 다발적 토큰 갱신 방지
    private val mutex = Mutex()

    override suspend fun getValidToken(): String = mutex.withLock {
        val stored = dataStore.data.first()[tokenKey]
        // 토큰이 있고 만료 60초 전까지는 그대로 사용
        if (!stored.isNullOrEmpty() && !isTokenExpiredSoon(stored)) return@withLock stored
        refreshOrLoginInternal()
    }

    override suspend fun forceRefresh(): String = mutex.withLock {
        refreshOrLoginInternal()
    }

    override suspend fun logout() {
        val refreshToken = dataStore.data.first()[refreshTokenKey] ?: return
        runCatching { api.logout(LogoutRequest(refreshToken)) }
        clearTokens()
    }

    /** mutex를 이미 보유한 상태에서 호출. 리프레시 실패 시 재로그인. */
    private suspend fun refreshOrLoginInternal(): String {
        val storedRefresh = dataStore.data.first()[refreshTokenKey]
        if (!storedRefresh.isNullOrEmpty()) {
            val response = runCatching { api.refreshToken(RefreshTokenRequest(storedRefresh)) }.getOrNull()
            // 서버가 HTTP 200 + 에러바디를 반환하면 Gson이 token을 null로 파싱함 → 유효하지 않으면 login()으로 fallback
            if (!response?.token.isNullOrEmpty()) {
                return saveAndReturn(response!!)
            }
        }
        return login()
    }

    override suspend fun reLogin() {
        mutex.withLock {
            clearTokens()
            login()
        }
    }

    override suspend fun validatePharmacy(bizNo: String): Pair<Boolean, String> =
        runCatching {
            val response = api.validatePharmacy(bizNo)
            response.valid to response.message
        }.getOrDefault(false to "")

    private suspend fun login(): String {
        val id = configRepository.getValue(ConfigKey.BIZ_NO, "")
        val password = configRepository.getValue(ConfigKey.PASSWORD, "")
        val response = api.login(LoginRequest(id, password))
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

    /**
     * JWT payload의 exp 클레임을 파싱해서 만료 60초 이내이면 true 반환.
     * 파싱 실패 시 만료된 것으로 간주.
     */
    private fun isTokenExpiredSoon(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return true
            val payload = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING)
            val json = String(payload, Charsets.UTF_8)
            val exp = Regex("\"exp\":(\\d+)").find(json)?.groupValues?.get(1)?.toLongOrNull()
                ?: return true
            val nowSeconds = System.currentTimeMillis() / 1000
            exp <= nowSeconds + 60
        } catch (e: Exception) {
            true
        }
    }
}
