package com.cresoty.catpospoint.presentation.setting

import android.net.Uri
import androidx.datastore.preferences.core.Preferences
import com.cresoty.catpospoint.presentation.Dialogs
import com.cresoty.catpospoint.ui.setting.SettingType

object SettingContract {

    data class State(
        val dialog: Dialogs = Dialogs.None,
        val selectedMenuIndex: Int = 0,
        val password: String = "",
        val isPasswordCorrect: Boolean = true,
        val isSavedToastVisible: Boolean = false,
        val editingSubTitle: String? = null,
        val editingThemeIndex: Int? = null,
        val customThemeImageUri: Uri? = null,
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
    }

    sealed interface Effect {
        data class OpenPreview(
            val theme: Int?,
            val subTitle: String?,
            val customImageUri: Uri?,
        ) : Effect
    }
}
