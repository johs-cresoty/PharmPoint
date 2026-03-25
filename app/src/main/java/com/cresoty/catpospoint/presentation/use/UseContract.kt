package com.cresoty.catpospoint.presentation.use

import com.cresoty.catpospoint.presentation.phoneNumberInput.PhoneNumberInputContract


object UseContract {
    data class State(
        val storeName: String = "",
        val payAmount: Int = 0,
        val usePoint: Int = 0,
        val balancePoint: Int = 0,
        val minPoint: Int = 0,
        val isMinPointEnabled: Boolean = false
    )

    sealed interface Event {
        data object GoToWaiting : Event
        data class OnNumberInput(val digit: String) : Event
        data object OnDeleteOne : Event
        data object OnDeleteAll : Event
        data class OnClickConfirm(val usePoint: Int) : Event
        data object OnClickClose : Event
    }

    sealed interface Effect {

    }
}