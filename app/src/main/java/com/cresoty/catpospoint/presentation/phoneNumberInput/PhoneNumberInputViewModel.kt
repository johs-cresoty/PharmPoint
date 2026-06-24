package com.cresoty.catpospoint.presentation.phoneNumberInput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.device.DeviceInfoProvider
import com.cresoty.catpospoint.domain.model.TransactionData
import com.cresoty.catpospoint.domain.model.command.EstimatePointCommand
import com.cresoty.catpospoint.domain.usecase.EstimatePointUseCase
import com.cresoty.catpospoint.domain.usecase.GetConfigUseCase
import com.cresoty.catpospoint.domain.usecase.GetCustomerUseCase
import com.cresoty.catpospoint.domain.usecase.GetPointBalanceUseCase
import com.cresoty.catpospoint.domain.usecase.SendCATCustomerInfoUseCase
import com.cresoty.catpospoint.domain.usecase.SendCATFailUseCase
import com.cresoty.catpospoint.domain.usecase.SendCATPhoneNumberUseCase
import com.cresoty.catpospoint.domain.usecase.UpsertCustomerPointUseCase
import com.cresoty.catpospoint.model.enums.PaymentType
import com.cresoty.catpospoint.model.enums.PointUseSource
import com.cresoty.catpospoint.model.state.ConfigState
import com.cresoty.catpospoint.presentation.result.ResultContract
import com.cresoty.catpospoint.presentation.result.ResultStatus
import com.cresoty.catpospoint.presentation.use.UseContract
import com.cresoty.catpospoint.toDecimalString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneNumberInputViewModel @Inject constructor(
    private val getConfigUseCase: GetConfigUseCase,
    private val estimatePointUseCase: EstimatePointUseCase,
    private val upsertCustomerPointUseCase: UpsertCustomerPointUseCase,
    private val getCustomerUseCase: GetCustomerUseCase,
    private val getPointBalanceUseCase: GetPointBalanceUseCase,
    private val sendCATPhoneNumberUseCase: SendCATPhoneNumberUseCase,
    private val sendCATCustomerInfoUseCase: SendCATCustomerInfoUseCase,
    private val sendCATFailUseCase: SendCATFailUseCase,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val reducer: PhoneNumberInputReducer,
) : ViewModel() {

    val configState = getConfigUseCase()
    private val _uiState = MutableStateFlow(PhoneNumberInputContract.State())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<PhoneNumberInputContract.Effect>(Channel.Factory.BUFFERED)
    val effect = _effect.receiveAsFlow()

    // Init 이벤트에서 전달된 파싱 완료 거래 데이터 (EstimatePointCommand / UpsertCustomerPoint 빌드용)
    private var transactionData: TransactionData? = null

    fun dispatch(event: PhoneNumberInputContract.Event) {
        if (event is PhoneNumberInputContract.Event.Init) transactionData = event.transactionData
        val (newState, effects) = reducer.reduce(_uiState.value, event)
        _uiState.value = newState
        effects.forEach { effect ->
            when (effect) {
                PhoneNumberInputContract.Effect.LoadData -> handleLoadData()
                PhoneNumberInputContract.Effect.RequestSavePoint -> requestSavePoint()
                is PhoneNumberInputContract.Effect.CheckCustomerExist -> checkCustomerExist(effect.phoneNumber)
                PhoneNumberInputContract.Effect.RequestNavigateToUsePoint -> navigateToUsePoint()
                PhoneNumberInputContract.Effect.ProceedAfterCustomerCheck -> proceedAfterCustomerCheck()
                is PhoneNumberInputContract.Effect.SendToCATPhoneNumber -> sendCATPhoneNumber(effect.phoneNumber)
                is PhoneNumberInputContract.Effect.SendToCATCustomerInfo -> sendCATCustomerInfo(
                    effect.phoneNumber
                )

                PhoneNumberInputContract.Effect.SendCATFail -> sendCATFail()
                else -> viewModelScope.launch { _effect.send(effect) }  // UI 네비게이션만 Route로
            }
        }
    }

    private fun handleLoadData() {
        val config = configState.value
        dispatch(PhoneNumberInputContract.Event.OnInitSuccess(config.bizNo, config.storeName))

        val mode = _uiState.value.mode
        if (mode !is PhoneNumberInputContract.Mode.Save) return

        viewModelScope.launch {
            val command = buildEstimateCommand(mode.paymentType, config) ?: return@launch
            estimatePointUseCase(command).collect { resource ->
                when (resource) {
                    is DataResource.Success -> resource.data?.let {
                        dispatch(
                            PhoneNumberInputContract.Event.OnEstimatePointSuccess(
                                estimatedPoint = it.pointAmount.toIntOrNull() ?: 0,
                                sleSeq = it.sleSeq,
                            )
                        )
                    }

                    is DataResource.Error -> {}
                    is DataResource.Loading -> {}
                }
            }
        }
    }

    private fun requestSavePoint() {
        val state = _uiState.value
        viewModelScope.launch {
            upsertCustomerPointUseCase(
                taxNo = state.bizNo,
                customerPhone = state.phoneNumber,
                transactionDate = state.trnDate,
                transactionUniqueNumber = state.sleSeq,
                transactionMethod = state.trnGubn,
                transactionTime = state.trnTime,
                transactionAmount = state.payAmount.toString(),
                approvalNumber = state.appNum,
                complexTranInfo = transactionData?.payments,
            ).collect { resource ->
                when (resource) {
                    is DataResource.Success -> {
                        val result = resource.data
                        val earnPoint = state.estimatedPoint
                        val config = configState.value
                        val subTitle = if (result.customerName?.isNotEmpty() == true) "${result.customerName} 님" else ""
                        val resultState = ResultContract.State(
                            status = ResultStatus.EARN_SUCCESS,
                            title = if (earnPoint > 0) "${earnPoint.toDecimalString()}P 적립완료" else "적립완료",
                            subTitle = subTitle,
                            pointTitle = "보유 포인트",
                            balancePoint = result.pointBalance,
                            timeOut = config.autoCloseTimeout,
                        )
                        dispatch(PhoneNumberInputContract.Event.OnSavePointSuccess(resultState))
                    }

                    is DataResource.Error -> dispatch(PhoneNumberInputContract.Event.OnSavePointError)
                    is DataResource.Loading -> {}
                }
            }
        }
    }

    /** 고객 존재 확인 후 결과에 따라 다음 화면으로 이동 */
    private fun proceedAfterCustomerCheck() {
        val mode = _uiState.value.mode as? PhoneNumberInputContract.Mode.Lookup ?: return
        if (mode.source == PointUseSource.MANUAL) {
            viewModelScope.launch {
                _effect.send(
                    PhoneNumberInputContract.Effect.GoToTheResultScreen(
                        buildCheckPointResultState()
                    )
                )
            }
        } else {
            navigateToUsePoint()
        }
    }

    private fun checkCustomerExist(phoneNumber: String) {
        val config = configState.value
        viewModelScope.launch {
            // 1단계: 등록 여부 확인 (GET /api/terminals/customers)
            getCustomerUseCase(
                computerName = deviceInfoProvider.computerName,
                posVersion = deviceInfoProvider.posVersion,
                taxNo = config.bizNo,
                customerHp = phoneNumber,
            ).collect { resource ->
                when (resource) {
                    is DataResource.Success -> {
                        if (resource.data == null) {
                            // LIST 비어있음 → 미등록 회원
                            dispatch(
                                PhoneNumberInputContract.Event.OnCustomerCheckResult(
                                    exists = false,
                                    balancePoint = 0,
                                    customerCode = "",
                                    customerName = ""
                                )
                            )
                        } else {
                            // 등록 회원 → 2단계: 상세 조회 (GET /api/terminals/customers/code)
                            fetchPointBalance(phoneNumber)
                        }
                    }

                    is DataResource.Error -> dispatch(
                        PhoneNumberInputContract.Event.OnCustomerCheckResult(
                            exists = false,
                            balancePoint = 0,
                            customerCode = "",
                            customerName = ""
                        )
                    )

                    is DataResource.Loading -> {}
                }
            }
        }
    }

    private suspend fun fetchPointBalance(phoneNumber: String) {
        val config = configState.value
        getPointBalanceUseCase(
            taxNo = config.bizNo,
            customerPhone = phoneNumber,
        ).collect { resource ->
            when (resource) {
                is DataResource.Success -> {
                    val balance = resource.data
                    dispatch(
                        PhoneNumberInputContract.Event.OnCustomerCheckResult(
                            exists = true,
                            balancePoint = balance.pointBalance.toIntOrNull() ?: 0,
                            customerCode = balance.customerCode,
                            customerName = balance.customerName,
                        )
                    )
                }

                is DataResource.Error -> dispatch(
                    PhoneNumberInputContract.Event.OnCustomerCheckResult(
                        exists = false,
                        balancePoint = 0,
                        customerCode = "",
                        customerName = ""
                    )
                )

                is DataResource.Loading -> {}
            }
        }
    }

    private fun navigateToUsePoint() {
        val state = _uiState.value
        val config = configState.value
        val source = (state.mode as? PhoneNumberInputContract.Mode.Lookup)?.source
            ?: return
        val usePointState = UseContract.State(
            source = source,
            phoneNumber = state.phoneNumber,
            customerCode = state.customerCode,
            customerName = state.customerName,
            storeName = state.storeName,
            payAmount = state.payAmount,
            balancePoint = state.balancePoint,
            minPoint = config.minPoint,
            isMinPointEnabled = config.isMinPointEnabled,
        )
        viewModelScope.launch {
            if (usePointState.isMinPointEnabled && usePointState.minPoint > usePointState.balancePoint || usePointState.balancePoint < 1) {
                val subTitle = if (usePointState.isMinPointEnabled) {
                    "${usePointState.storeName}\n최소 ${usePointState.minPoint.toDecimalString()}P부터 사용 가능합니다."
                } else {
                    usePointState.storeName
                }
                val resultState = ResultContract.State(
                    status = ResultStatus.USE_UNAVAILABLE,
                    title = "포인트 부족",
                    subTitle = subTitle,
                    pointTitle = "잔여 포인트",
                    balancePoint = usePointState.balancePoint,
                    timeOut = config.autoCloseTimeout,
                )
                // 캣포스(PC) CAT|006 흐름에서 포인트 부족인 경우, sendCATFail 응답 전송
                if (source == PointUseSource.CAT) {
                    sendCATFailUseCase()
                }
                _effect.send(PhoneNumberInputContract.Effect.GoToTheResultScreen(resultState))
            } else {
                _effect.send(PhoneNumberInputContract.Effect.GoToUsePointScreen(usePointState))
            }
        }
    }

    private fun sendCATPhoneNumber(phoneNumber: String) {
        sendCATPhoneNumberUseCase(phoneNumber)
        dispatch(PhoneNumberInputContract.Event.GoToTheWaitingScreen)
    }

    private fun sendCATCustomerInfo(phoneNumber: String) {
        viewModelScope.launch {
            sendCATCustomerInfoUseCase(configState.value.bizNo, phoneNumber).collect { resource ->
                when (resource) {
                    is DataResource.Success -> dispatch(PhoneNumberInputContract.Event.GoToTheWaitingScreen)
                    is DataResource.Error -> dispatch(PhoneNumberInputContract.Event.OnSavePointError)
                    is DataResource.Loading -> {}
                }
            }
        }
    }

    private fun buildCheckPointResultState(): ResultContract.State {
        val state = _uiState.value
        val config = configState.value
        return ResultContract.State(
            status = ResultStatus.FETCH_SUCCESS,
            subTitle = if (state.customerName.isNotEmpty()) "${state.customerName} 님" else "",
            pointTitle = "보유 포인트",
            balancePoint = state.balancePoint,
            timeOut = config.autoCloseTimeout,
        )
    }

    private fun sendCATFail() {
        sendCATFailUseCase()
        dispatch(PhoneNumberInputContract.Event.GoToTheWaitingScreen)
    }

    private fun buildEstimateCommand(
        paymentType: PaymentType,
        config: ConfigState,
    ): EstimatePointCommand? {
        val td = transactionData ?: return null
        return when (paymentType) {
            PaymentType.SINGLE -> EstimatePointCommand.Single(
                taxNo = config.bizNo,
                computerName = deviceInfoProvider.computerName,
                posVersion = deviceInfoProvider.posVersion,
                trnDate = td.trnDate,
                trnGubn = td.trnGubn,
                trnAmt = td.payAmount.toString(),
                appNum = td.appNum,
            )

            PaymentType.MULTIPLE -> {
                val payments = td.payments ?: return null
                EstimatePointCommand.Complex(
                    taxNo = config.bizNo,
                    computerName = deviceInfoProvider.computerName,
                    posVersion = deviceInfoProvider.posVersion,
                    payments = payments,
                )
            }
        }
    }
}
