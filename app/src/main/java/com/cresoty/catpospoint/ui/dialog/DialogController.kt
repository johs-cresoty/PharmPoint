package com.cresoty.catpospoint.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpospoint.presentation.Dialogs
import com.cresoty.catpospoint.presentation.app.AppContract
import com.cresoty.catpospoint.presentation.app.AppViewModel
import com.cresoty.catpospoint.presentation.setting.SettingViewModel

@Composable
fun DialogController(
    settingVm: SettingViewModel = hiltViewModel(),
    appVm: AppViewModel = hiltViewModel(),
) {
    val settingState by settingVm.uiState.collectAsStateWithLifecycle()
    val appState by appVm.uiState.collectAsStateWithLifecycle()

    // 앱 수준 다이얼로그(업데이트) 우선, 없으면 설정 다이얼로그
    val effectiveDialog = if (appState.dialog != Dialogs.None) appState.dialog else settingState.dialog

    when (val dialog = effectiveDialog) {
        Dialogs.None -> Unit
        Dialogs.Setting -> SettingDialog(vm = settingVm)
        Dialogs.InputPassword -> AdminLoginDialog(vm = settingVm)
        Dialogs.UpdateRequired -> UpdateRequiredDialog()
        is Dialogs.UpdateBlocked -> UpdateBlockedDialog(
            messageTitle = dialog.messageTitle,
            message = dialog.message,
            onAcceptUpdate = { appVm.dispatch(AppContract.Event.OnAcceptUpdate) }
        )
    }
}
