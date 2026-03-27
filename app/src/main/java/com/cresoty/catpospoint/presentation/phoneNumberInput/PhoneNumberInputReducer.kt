package com.cresoty.catpospoint.presentation.phoneNumberInput

import com.cresoty.catpospoint.model.enums.PointUseSource
import javax.inject.Inject

class PhoneNumberInputReducer @Inject constructor() {
    fun reduce(
        state: PhoneNumberInputContract.State,
        event: PhoneNumberInputContract.Event
    ): Pair<PhoneNumberInputContract.State, List<PhoneNumberInputContract.Effect>> {
        return when (event) {
            is PhoneNumberInputContract.Event.Init -> {
                val td = event.transactionData
                val newState = when (event.mode) {
                    is PhoneNumberInputContract.Mode.Save -> state.copy(
                        mode = event.mode,
                        payAmount = td?.payAmount ?: 0,
                        trnDate = td?.trnDate ?: "",
                        trnTime = td?.trnTime ?: "",
                        appNum = td?.appNum ?: "",
                        trnGubn = td?.trnGubn ?: "",
                    )

                    is PhoneNumberInputContract.Mode.Lookup -> state.copy(
                        mode = event.mode,
                        payAmount = td?.payAmount ?: 0,
                    )

                    is PhoneNumberInputContract.Mode.CatRequestNum -> state.copy(mode = event.mode)
                    is PhoneNumberInputContract.Mode.CatRequestCustomer -> state.copy(mode = event.mode)
                }
                newState to listOf(PhoneNumberInputContract.Effect.LoadData)
            }

            is PhoneNumberInputContract.Event.OnInitSuccess -> {
                state.copy(bizNo = event.bizNo, storeName = event.storeName) to emptyList()
            }

            is PhoneNumberInputContract.Event.OnEstimatePointSuccess -> {
                state.copy(
                    estimatedPoint = event.estimatedPoint,
                    sleSeq = event.sleSeq
                ) to emptyList()
            }

            is PhoneNumberInputContract.Event.OnNumberInput -> {
                val newPhone = if (state.phoneNumber.length < 11)
                    state.phoneNumber + event.digit
                else state.phoneNumber
                val isLookup = state.mode is PhoneNumberInputContract.Mode.Lookup
                if (isLookup) {
                    val shouldCheck = newPhone.length == 11
                    val effects = if (shouldCheck)
                        listOf(PhoneNumberInputContract.Effect.CheckCustomerExist(newPhone))
                    else emptyList()
                    state.copy(
                        phoneNumber = newPhone,
                        isExistChecking = shouldCheck,
                        isCustomerExist = false,
                    ) to effects
                } else {
                    state.copy(phoneNumber = newPhone) to emptyList()
                }
            }

            PhoneNumberInputContract.Event.OnDeleteOne -> {
                val isLookup = state.mode is PhoneNumberInputContract.Mode.Lookup
                val newState = if (isLookup)
                    state.copy(
                        phoneNumber = state.phoneNumber.dropLast(1),
                        isCustomerExist = false,
                        isExistChecking = false
                    )
                else
                    state.copy(phoneNumber = state.phoneNumber.dropLast(1))
                newState to emptyList()
            }

            PhoneNumberInputContract.Event.OnDeleteAll -> {
                val isLookup = state.mode is PhoneNumberInputContract.Mode.Lookup
                val newState = if (isLookup)
                    state.copy(phoneNumber = "", isCustomerExist = false, isExistChecking = false)
                else
                    state.copy(phoneNumber = "")
                newState to emptyList()
            }

            is PhoneNumberInputContract.Event.OnCustomerCheckResult -> {
                state.copy(
                    isExistChecking = false,
                    isCustomerExist = event.exists,
                    balancePoint    = event.balancePoint,
                    customerCode    = event.customerCode,
                    isLoading       = false,
                ) to emptyList()
            }

            is PhoneNumberInputContract.Event.OnClickConfirm -> {
                when (state.mode) {
                    is PhoneNumberInputContract.Mode.Save ->
                        state.copy(isLoading = true) to listOf(PhoneNumberInputContract.Effect.RequestSavePoint)

                    is PhoneNumberInputContract.Mode.Lookup ->
                        if ((state.mode as PhoneNumberInputContract.Mode.Lookup).source == PointUseSource.MANUAL)
                            state to listOf(PhoneNumberInputContract.Effect.GoToTheResultScreen(event.resultState!!))
                        else
                            state to listOf(PhoneNumberInputContract.Effect.RequestNavigateToUsePoint)

                    PhoneNumberInputContract.Mode.CatRequestNum ->
                        state to listOf(PhoneNumberInputContract.Effect.SendToCATPhoneNumber(event.phoneNumber))

                    PhoneNumberInputContract.Mode.CatRequestCustomer ->
                        state.copy(isLoading = true) to listOf(
                            PhoneNumberInputContract.Effect.SendToCATCustomerInfo(
                                state.bizNo,
                                event.phoneNumber
                            )
                        )
                }
            }

            is PhoneNumberInputContract.Event.OnSavePointSuccess ->
                state.copy(isLoading = false) to listOf(
                    PhoneNumberInputContract.Effect.GoToTheResultScreen(
                        event.resultState
                    )
                )

            PhoneNumberInputContract.Event.OnSavePointError ->
                state.copy(isLoading = false) to emptyList()

            PhoneNumberInputContract.Event.OnClickClose ->
                state to listOf(PhoneNumberInputContract.Effect.SendCATFail)

            PhoneNumberInputContract.Event.GoToTheWaitingScreen ->
                state to listOf(PhoneNumberInputContract.Effect.GoToTheWaitingScreen)

            PhoneNumberInputContract.Event.OnMaskToggle -> {
                state.copy(isMasked = !state.isMasked) to emptyList()
            }

            is PhoneNumberInputContract.Event.OnClickCheckBox -> {
                state.copy(isCheckBox = event.isCheck) to emptyList()
            }

            PhoneNumberInputContract.Event.Loading -> state.copy(isLoading = true) to emptyList()
        }
    }
}
