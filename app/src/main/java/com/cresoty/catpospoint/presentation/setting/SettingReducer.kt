package com.cresoty.catpospoint.presentation.setting

import com.cresoty.catpospoint.presentation.Dialogs
import javax.inject.Inject

class SettingReducer @Inject constructor() {

    fun reduce(
        state: SettingContract.State,
        event: SettingContract.Event,
    ): Pair<SettingContract.State, List<SettingContract.Effect>> {
        return when (event) {

            SettingContract.Event.OpenSettingDialog ->
                state.copy(dialog = Dialogs.Setting) to emptyList()

            SettingContract.Event.OpenPasswordDialog ->
                state.copy(
                    dialog = Dialogs.InputPassword,
                    password = "",
                    isPasswordCorrect = true,
                ) to emptyList()

            // bizNo 빈값 가드는 ViewModel에서 처리 — Reducer는 순수 상태 리셋만 담당
            SettingContract.Event.CloseDialog ->
                state.copy(
                    dialog = Dialogs.None,
                    password = "",
                    selectedMenuIndex = 0,
                    isPasswordCorrect = true,
                    editingSubTitle = null,
                    editingThemeIndex = null,
                    isPharmacyInvalid = false,
                    isValidatingPharmacy = false,
                    validatingPharmacyMessage = "",
                ) to emptyList()

            SettingContract.Event.ResetPharmacyInvalid ->
                state.copy(isPharmacyInvalid = false, validatingPharmacyMessage = "") to emptyList()

            is SettingContract.Event.SelectMenu ->
                state.copy(selectedMenuIndex = event.index) to emptyList()

            is SettingContract.Event.InputPassword ->
                if (state.password.length < 5)
                    state.copy(password = state.password + event.digit) to emptyList()
                else
                    state to emptyList()

            SettingContract.Event.DeleteLastPassword ->
                state.copy(password = state.password.dropLast(1)) to emptyList()

            SettingContract.Event.DeleteAllPassword ->
                state.copy(password = "") to emptyList()

            // 사이드이펙트 이벤트는 ViewModel에서 차단 후 처리 — Reducer에 도달하지 않음
            else -> error("Unexpected event in SettingReducer: $event")
        }
    }
}
