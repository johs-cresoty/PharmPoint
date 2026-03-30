package com.cresoty.catpospoint.presentation.setting

import android.net.Uri
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cresoty.catpospoint.data.repository.ConfigKey
import com.cresoty.catpospoint.data.repository.ConfigRepository
import com.cresoty.catpospoint.presentation.Dialogs
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
        when (event) {
            SettingContract.Event.OpenSettingDialog -> openSettingDialog()
            SettingContract.Event.OpenPasswordDialog -> openPasswordDialog()
            SettingContract.Event.CloseDialog -> closeDialog()
            is SettingContract.Event.SelectMenu -> selectMenu(event.index)
            is SettingContract.Event.InputPassword -> inputPassword(event.digit)
            SettingContract.Event.DeleteLastPassword -> deleteLastPassword()
            SettingContract.Event.DeleteAllPassword -> deleteAllPassword()
            is SettingContract.Event.ConfirmPassword -> confirmPassword(event.input)
            is SettingContract.Event.SaveSetting -> saveSetting(event.type, event.data)
            is SettingContract.Event.StartPreview -> startPreview(event.theme, event.subTitle, event.customImageUri)
            is SettingContract.Event.CropCustomThemeImage -> cropCustomThemeImage(event.uri)
        }
    }

    private fun openSettingDialog() {
        _uiState.update { it.copy(dialog = Dialogs.Setting) }
    }

    private fun openPasswordDialog() {
        _uiState.update {
            it.copy(
                dialog = Dialogs.InputPassword,
                password = "",
                isPasswordCorrect = true,
            )
        }
    }

    private fun closeDialog() {
        // 사업자번호 미저장 상태에서는 다이얼로그 닫기 불가
        if (configState.value.bizNo.isEmpty()) return

        _uiState.update {
            it.copy(
                dialog = Dialogs.None,
                password = "",
                selectedMenuIndex = 0,
                isPasswordCorrect = true,
                editingSubTitle = null,
                editingThemeIndex = null,
            )
        }
    }

    private fun selectMenu(index: Int) {
        _uiState.update { it.copy(selectedMenuIndex = index) }
    }

    private fun inputPassword(digit: String) {
        val current = _uiState.value.password
        if (current.length <= 4) {
            _uiState.update { it.copy(password = current + digit) }
        }
    }

    private fun deleteLastPassword() {
        _uiState.update { it.copy(password = it.password.dropLast(1)) }
    }

    private fun deleteAllPassword() {
        _uiState.update { it.copy(password = "") }
    }

    /** 비밀번호 확인: bizNo 뒤 5자리와 비교 */
    private fun confirmPassword(input: String) {
        viewModelScope.launch {
            val correctPw = configState.value.bizNo.safeSubString(5)
            if (input == correctPw) {
                _uiState.update {
                    it.copy(
                        dialog = Dialogs.Setting,
                        password = "",
                        isPasswordCorrect = true,
                    )
                }
            } else {
                _uiState.update { it.copy(isPasswordCorrect = false) }
            }
        }
    }

    private fun saveSetting(type: SettingType, data: Map<Preferences.Key<*>, Any>) {
        viewModelScope.launch {
            when (type) {
                SettingType.STORE_INFO -> {
                    val bizNo = data[ConfigKey.BIZ_NO] as String
                    val storeName = data[ConfigKey.STORE_NAME] as String
                    configRepo.putValue(ConfigKey.BIZ_NO, bizNo)
                    configRepo.putValue(ConfigKey.STORE_NAME, storeName)
                }
                SettingType.POINT_USE -> {
                    val isPointUse = data[ConfigKey.IS_MIN_POINT_ENABLED] as Boolean
                    val minPoint = data[ConfigKey.MINIMUM_POINT] as Int
                    configRepo.putValue(ConfigKey.IS_MIN_POINT_ENABLED, isPointUse)
                    configRepo.putValue(ConfigKey.MINIMUM_POINT, minPoint)
                }
                SettingType.SCREEN_TIMEOUT -> {
                    val timeout = data[ConfigKey.SCREEN_TIMEOUT] as Int
                    configRepo.putValue(ConfigKey.SCREEN_TIMEOUT, timeout)
                }
                SettingType.THEME -> {
                    val themeIndex = data[ConfigKey.MAIN_THEME] as Int
                    val subTitle = data[ConfigKey.SUB_TITLE] as String
                    val customUri = _uiState.value.customThemeImageUri
                        ?.takeIf { it != Uri.EMPTY }
                        ?.toString() ?: ""
                    configRepo.putValue(ConfigKey.MAIN_THEME, themeIndex)
                    configRepo.putValue(ConfigKey.SUB_TITLE, subTitle)
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
