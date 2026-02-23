package com.cresoty.catpossignpad.view.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpossignpad.model.interfaces.Dialogs
import com.cresoty.catpossignpad.view.composable.AdminLoginDialog
import com.cresoty.catpossignpad.view.composable.SettingDialog

@Composable
fun DialogController() {
    val controller = LocalController.current
    val setting by controller.settingState.collectAsStateWithLifecycle()
    val dialog = setting.dialog

    when (dialog) {
        Dialogs.None -> Unit
        Dialogs.Setting -> {
            SettingDialog()
        }

        Dialogs.InputPassword -> {
            AdminLoginDialog()
        }
    }
}