package com.cresoty.catpospoint.presentation.phoneNumberInput

import com.cresoty.catpospoint.model.enums.PointUseSource


object PhoneNumberInputContract {
    sealed class Mode {
        data class Lookup(val source: PointUseSource) : Mode()  // 고객 조회
        data class Save(val source: PointUseSource)   : Mode()  // 포인트 적립
    }
    data class State(
        val mode: Mode = Mode.Save(PointUseSource.TERMINAL),
        val phoneNumber: String = "010",
        val bizNo: String = "",
        val storeName: String = "",
        val payAmount: Int = 0,
        val estimatedPoint: Int = 0,
        var isCustomerExist: Boolean = false,
        val isExistChecking: Boolean = false,
        val isMasked: Boolean = true,
        val isCheckBox: Boolean = true
    )

    sealed interface Event {
        data object Init : Event
        data class OnInitSuccess(val bizNo: String, val storeName: String) : Event
        data class OnClickConfirm(val phoneNumber: String) : Event
        data object OnClickClose : Event
        data object GoToTheWaitingScreen : Event
        data class OnClickCheckBox(val isCheck: Boolean) : Event
        data class OnNumberInput(val digit: String) : Event
        data object OnDeleteOne : Event
        data object OnDeleteAll : Event
        data object OnMaskToggle : Event
    }

    sealed interface Effect {
        data object LoadData : Effect
        data class SendToCATCustomerInfo(val bizNo: String, val phoneNumber: String) : Effect
        data class SendToCATPhoneNumber(val phoneNumber: String) : Effect
        data object SendCATFail : Effect
        data object GoToTheWaitingScreen : Effect
    }
}