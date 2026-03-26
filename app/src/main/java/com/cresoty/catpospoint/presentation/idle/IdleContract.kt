package com.cresoty.catpospoint.presentation.idle

import com.cresoty.catpospoint.model.enums.PaymentType
import com.cresoty.catpospoint.model.enums.PointUseSource


object IdleContract {
    data class State(
        val isPreview: Boolean = false,
    )

    sealed interface Event {
        data object Init : Event
        data object OnTripleTap : Event
        data object OnClickPointBalance : Event
//        data class GoToInputPhoneNumber(val pointUseSource: PointUseSource, val data: List<String>) : Event
//        data class GoToUsePoint(val pointUseSource: PointUseSource, val data: List<String>) : Event
//        data class GoToEarnPoint(val pointUseSource: PointUseSource, val paymentType: PaymentType, val data: List<String>) : Event
//        data class GoToCheckPoint(val pointUseSource: PointUseSource, val data: List<String>) : Event
    }

    sealed interface Effect {
        /** bizNo 유무를 체크한 뒤 Route에서 적절한 다이얼로그를 열어야 할 때 발행 */
        data class ShowSettingDialog(val requiresPassword: Boolean) : Effect
        data object NavigateToPointBalance : Effect
    }
}