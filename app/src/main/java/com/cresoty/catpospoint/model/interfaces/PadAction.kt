package com.cresoty.catpospoint.model.interfaces

import androidx.datastore.preferences.core.Preferences
import com.cresoty.catpospoint.model.enums.PointDeltaProcess
import com.cresoty.catpospoint.model.enums.PointQuickInputType
import com.cresoty.catpospoint.view.composable.list.SettingType

sealed interface PadAction {
    // Dialog관련
//    data class OpenDialog(val dialog : Dialogs) : PadAction
    data class OnClickSettingMenu(val index : Int) : PadAction
    data class OnClickSaveSetting(val type : SettingType, val data : Map<Preferences.Key<*>, Any>) : PadAction
    data class OnClickPasswordPad(val number : String) : PadAction
    data class OnClickNumberPad(val number : String) : PadAction
    data class OnClickAdminLogin(val input : String) : PadAction
    data class OnClickShowPreview(val preIndex: Int, val subTitle : String) : PadAction
    data class OnClickPointNext(/*val current : PointDeltaProcess, */val next : PointDeltaProcess) : PadAction
    data class OnClickAmountQuickButton(val type : PointQuickInputType) : PadAction

    data object OnClickSetting : PadAction
    data object OnClickDeleteLastPassword : PadAction
    data object OnClickDeleteAllPassword : PadAction
    data object OnClickDeleteLastPhoneNumber : PadAction
    data object OnClickDeleteAllPhoneNumber : PadAction
    data object CloseDialog : PadAction
    data object OnClickClosePreview : PadAction
    data object OnClickPersonalInfoUse : PadAction
    data object OnClickMaskingToggle : PadAction

    data object RequestSavePoint : PadAction
    data object RequestPointBalanceCheck : PadAction
    data object RequestCustomerVerify : PadAction
    data object SendToTerminalPointUse : PadAction

    data object RequestExpectSaveAmount : PadAction
    data object SendToCATCustomerInfo: PadAction
    data object SendToCATPhoneNumber: PadAction
    data object SendCATFail : PadAction

}