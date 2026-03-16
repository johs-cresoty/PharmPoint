package com.cresoty.catpospoint.ui.phoneNumberInput

import com.cresoty.catpospoint.presentation.phoneNumberInput.PhoneNumberInputContract
import javax.inject.Inject

class PhoneNumberInputReducer @Inject constructor() {
    fun reduce(
        state: PhoneNumberInputContract.State,
        event: PhoneNumberInputContract.Event
    ): Pair<PhoneNumberInputContract.State, List<PhoneNumberInputContract.Effect>> {
        return when (event) {
            PhoneNumberInputContract.Event.Init -> {
                state to listOf(PhoneNumberInputContract.Effect.LoadData)
            }

            is PhoneNumberInputContract.Event.OnInitSuccess -> {
                state.copy(bizNo = event.bizNo, storeName = event.storeName) to emptyList()
            }

            is PhoneNumberInputContract.Event.OnNumberInput -> {
                val newPhone = if (state.phoneNumber.length < 11)
                    state.phoneNumber + event.digit
                else state.phoneNumber
                state.copy(phoneNumber = newPhone) to emptyList()
            }

            PhoneNumberInputContract.Event.OnDeleteOne -> {
                state.copy(phoneNumber = state.phoneNumber.dropLast(1)) to emptyList()
            }

            PhoneNumberInputContract.Event.OnDeleteAll -> {
                state.copy(phoneNumber = "") to emptyList()
            }

            is PhoneNumberInputContract.Event.OnClickConfirm -> {
                state to listOf(PhoneNumberInputContract.Effect.SendToCATPhoneNumber(event.phoneNumber))
            }

            PhoneNumberInputContract.Event.OnClickClose -> state to listOf(PhoneNumberInputContract.Effect.SendCATFail)
            PhoneNumberInputContract.Event.GoToTheWaitingScreen -> state to listOf(
                PhoneNumberInputContract.Effect.GoToTheWaitingScreen
            )

            PhoneNumberInputContract.Event.OnMaskToggle -> {
                state.copy(isMasked = !state.isMasked) to emptyList()
            }

            is PhoneNumberInputContract.Event.OnClickCheckBox -> {
                state.copy(isCheckBox = event.isCheck) to emptyList()
            }
        }

    }
}