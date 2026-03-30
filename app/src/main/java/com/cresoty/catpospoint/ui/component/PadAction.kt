package com.cresoty.catpospoint.ui.component

import com.cresoty.catpospoint.model.enums.PointQuickInputType

/**
 * NumberPad (ui/component) 의 콜백 타입.
 * 설정/다이얼로그 관련 액션은 SettingViewModel 로 이전됨.
 */
sealed interface PadAction {
    data class OnClickNumberPad(val number: String) : PadAction
    data class OnClickAmountQuickButton(val type: PointQuickInputType) : PadAction

    data object OnClickDeleteLastPhoneNumber : PadAction
    data object OnClickDeleteAllPhoneNumber : PadAction
    data object OnClickPersonalInfoUse : PadAction
    data object OnClickMaskingToggle : PadAction
}
