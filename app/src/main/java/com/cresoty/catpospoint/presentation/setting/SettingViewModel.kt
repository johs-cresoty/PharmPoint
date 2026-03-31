package com.cresoty.catpospoint.presentation.setting

import android.net.Uri
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cresoty.catpospoint.data.repository.ConfigKey
import com.cresoty.catpospoint.data.repository.ConfigRepository
import com.cresoty.catpospoint.safeSubString
import com.cresoty.catpospoint.ui.setting.SettingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val configRepo: ConfigRepository,
    private val reducer: SettingReducer,
) : ViewModel() {

    /** 설정 관련 config 값 (서브 컴포넌트에서 읽기용) */
    val configState: StateFlow<com.cresoty.catpospoint.model.state.ConfigState> = configRepo.configState

    private val _uiState = MutableStateFlow(SettingContract.State())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<SettingContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        // 저장된 사용자 지정 이미지 URI 복원
        viewModelScope.launch {
            val savedUri = configRepo.getValue(ConfigKey.CUSTOM_IMAGE_URI, "")
            if (savedUri.isNotEmpty()) {
                _uiState.update { it.copy(customThemeImageUri = Uri.parse(savedUri)) }
            }
        }
    }

    fun dispatch(event: SettingContract.Event) {
        // 사이드이펙트/비즈니스 로직 이벤트는 ViewModel에서 직접 처리
        when (event) {
            SettingContract.Event.CloseDialog -> {
                // 사업자번호 미저장 상태에서는 닫기 불가
                if (configState.value.bizNo.isEmpty()) return
            }
            is SettingContract.Event.ConfirmPassword -> { confirmPassword(event.input); return }
            is SettingContract.Event.SaveSetting -> { saveSetting(event.type, event.data); return }
            is SettingContract.Event.StartPreview -> { startPreview(event.theme, event.subTitle, event.customImageUri); return }
            is SettingContract.Event.CropCustomThemeImage -> { cropCustomThemeImage(event.uri); return }
            else -> {}
        }

        // 순수 상태 변환은 Reducer에 위임
        val (newState, effects) = reducer.reduce(_uiState.value, event)
        _uiState.value = newState
        effects.forEach { effect ->
            viewModelScope.launch { _effect.send(effect) }
        }
    }

    /** 비밀번호 확인: bizNo 뒤 5자리와 비교 */
    private fun confirmPassword(input: String) {
        val correctPw = configState.value.bizNo.safeSubString(5)
        if (input == correctPw) {
            _uiState.update {
                it.copy(
                    dialog = com.cresoty.catpospoint.presentation.Dialogs.Setting,
                    password = "",
                    isPasswordCorrect = true,
                )
            }
        } else {
            _uiState.update { it.copy(isPasswordCorrect = false) }
        }
    }

    private fun saveSetting(type: SettingType, data: Map<Preferences.Key<*>, Any>) {
        viewModelScope.launch {
            when (type) {
                SettingType.STORE_INFO -> {
                    configRepo.putValue(ConfigKey.BIZ_NO, data[ConfigKey.BIZ_NO] as String)
                    configRepo.putValue(ConfigKey.STORE_NAME, data[ConfigKey.STORE_NAME] as String)
                }
                SettingType.POINT_USE -> {
                    configRepo.putValue(ConfigKey.IS_MIN_POINT_ENABLED, data[ConfigKey.IS_MIN_POINT_ENABLED] as Boolean)
                    configRepo.putValue(ConfigKey.MINIMUM_POINT, data[ConfigKey.MINIMUM_POINT] as Int)
                }
                SettingType.SCREEN_TIMEOUT -> {
                    configRepo.putValue(ConfigKey.SCREEN_TIMEOUT, data[ConfigKey.SCREEN_TIMEOUT] as Int)
                }
                SettingType.THEME -> {
                    val customUri = _uiState.value.customThemeImageUri
                        ?.takeIf { it != Uri.EMPTY }
                        ?.toString() ?: ""
                    configRepo.putValue(ConfigKey.MAIN_THEME, data[ConfigKey.MAIN_THEME] as Int)
                    configRepo.putValue(ConfigKey.SUB_TITLE, data[ConfigKey.SUB_TITLE] as String)
                    configRepo.putValue(ConfigKey.CUSTOM_IMAGE_URI, customUri)
                }
            }

            _uiState.update { it.copy(isSavedToastVisible = true) }
            delay(2000)
            _uiState.update { it.copy(isSavedToastVisible = false) }
        }
    }

    private fun startPreview(theme: Int?, subTitle: String?, customImageUri: Uri?) {
        viewModelScope.launch {
            _effect.send(SettingContract.Effect.OpenPreview(theme, subTitle, customImageUri))
        }
    }

    private fun cropCustomThemeImage(uri: Uri) {
        // 기존 파일 삭제
        _uiState.value.customThemeImageUri?.path?.let { File(it).delete() }
        _uiState.update { it.copy(customThemeImageUri = uri) }
    }
}
