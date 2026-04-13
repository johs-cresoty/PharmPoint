package com.cresoty.catpospoint.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cresoty.catpospoint.model.state.ConfigState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

val Context.dataStore by preferencesDataStore(name = "app_config")

object ConfigKey {
    val STORE_NAME = stringPreferencesKey("store_name")
    val BIZ_NO = stringPreferencesKey("biz_no")
    val SUB_TITLE = stringPreferencesKey("sub_title")
    val CUSTOM_IMAGE_URI = stringPreferencesKey("custom_image_uri")

    val MAIN_THEME = intPreferencesKey("main_theme")    // 0: A / 1: B / 2: C / 3: D
    val MINIMUM_POINT = intPreferencesKey("minimum_point")
    val SCREEN_TIMEOUT = intPreferencesKey("screen_timeout")
    val MIN_AMOUNT = intPreferencesKey("min_amount")                // 최소금액

    val IS_ID_VERIFY = booleanPreferencesKey("is_identification")  // true : 사용 / false : 미사용
    val IS_MIN_POINT_ENABLED = booleanPreferencesKey("is_min_point_enabled")        // 최소 포인트 사용 여부
    val IS_SAVE = booleanPreferencesKey("is_save")                  // 적립 여부
    val BRIGHTNESS = floatPreferencesKey("brightness")              // 화면 밝기 (0.1 ~ 1.0)
    val PASSWORD = stringPreferencesKey("password")                 // 관리자 비밀번호 (AES 암호화)
}

class ConfigRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    appScope: CoroutineScope
) {
    val configState: StateFlow<ConfigState> = dataStore.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { p ->
            ConfigState(
                bizNo = p[ConfigKey.BIZ_NO]?.takeIf { it.isNotBlank() } ?: "",
                storeName = p[ConfigKey.STORE_NAME] ?: "",
                themeIndex = p[ConfigKey.MAIN_THEME] ?: 0,
                subTitle = p[ConfigKey.SUB_TITLE] ?: "",
                customImageUri = p[ConfigKey.CUSTOM_IMAGE_URI] ?: "",
                timeout = p[ConfigKey.SCREEN_TIMEOUT] ?: 5,
                minPoint = p[ConfigKey.MINIMUM_POINT] ?: 1000,
                minAmount = p[ConfigKey.MIN_AMOUNT] ?: 20000,
                isIdVerify = p[ConfigKey.IS_ID_VERIFY] ?: false,
                isMinPointEnabled = p[ConfigKey.IS_MIN_POINT_ENABLED] ?: true,
                isSave = p[ConfigKey.IS_SAVE] ?: true,
                brightness = p[ConfigKey.BRIGHTNESS] ?: 1.0f,
                password = p[ConfigKey.PASSWORD] ?: ""
            )
        }
        .distinctUntilChanged()
        .stateIn(
            scope = appScope,
            started = SharingStarted.Eagerly,
            initialValue = ConfigState()
        )

    suspend fun <T> putValue(key: Preferences.Key<T>, value: T) {
        dataStore.edit { it[key] = value }
    }

    suspend fun <T> getValue(key: Preferences.Key<T>, default: T): T {
        return dataStore.data.first()[key] ?: default
    }
}
