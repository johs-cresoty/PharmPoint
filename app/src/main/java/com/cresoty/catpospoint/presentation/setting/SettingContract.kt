package com.cresoty.catpospoint.presentation.setting

import android.net.Uri
import androidx.datastore.preferences.core.Preferences
import com.cresoty.catpospoint.domain.model.UpdateInfo
import com.cresoty.catpospoint.presentation.Dialogs
import com.cresoty.catpospoint.ui.setting.SettingType

object SettingContract {

    /** 업데이트 메뉴 진입 시 버전 체크 결과 */
    sealed interface UpdateCheckState {
        data object Idle : UpdateCheckState
        data object Loading : UpdateCheckState
        data class NeedUpdate(val updateInfo: UpdateInfo) : UpdateCheckState
        data object UpToDate : UpdateCheckState
        data object Error : UpdateCheckState
    }

    data class State(
        val dialog: Dialogs = Dialogs.None,
        val selectedMenuIndex: Int = 0,
        val password: String = "",
        val isPasswordCorrect: Boolean = true,
        val isSavedToastVisible: Boolean = false,
        val editingSubTitle: String? = null,
        val editingThemeIndex: Int? = null,
        val customThemeImageUri: Uri? = null,
        val updateCheckState: UpdateCheckState = UpdateCheckState.Idle,
    )

    sealed interface Event {
        data object OpenSettingDialog : Event
        data object OpenPasswordDialog : Event
        data object CloseDialog : Event
        data class SelectMenu(val index: Int) : Event
        data class InputPassword(val digit: String) : Event
        data object DeleteLastPassword : Event
        data object DeleteAllPassword : Event
        data class ConfirmPassword(val input: String) : Event
        data class SaveSetting(val type: SettingType, val data: Map<Preferences.Key<*>, Any>) : Event
        data class StartPreview(val theme: Int?, val subTitle: String?, val customImageUri: Uri?) : Event
        data class CropCustomThemeImage(val uri: Uri) : Event
        /** 업데이트 메뉴 진입 시 버전 체크 요청 */
        data object CheckUpdate : Event
        /** 업데이트 버튼 클릭 */
        data class OnClickSettingUpdate(val installUrl: String) : Event
    }

    sealed interface Effect {
        data class OpenPreview(
            val theme: Int?,
            val subTitle: String?,
            val customImageUri: Uri?,
        ) : Effect
        /** 설정 화면에서 업데이트 시작 → AppViewModel로 라우팅 */
        data class StartUpdate(val installUrl: String) : Effect
    }
}
