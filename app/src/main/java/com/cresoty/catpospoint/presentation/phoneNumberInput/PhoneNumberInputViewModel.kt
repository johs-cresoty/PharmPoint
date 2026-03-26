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
        val finalEvent = if (
            event is PhoneNumberInputContract.Event.OnClickConfirm &&
            (_uiState.value.mode as? PhoneNumberInputContract.Mode.Lookup)?.source == PointUseSource.MANUAL
        ) {
            event.copy(resultState = buildCheckPointResultState())
        } else event
        val (newState, effects) = reducer.reduce(_uiState.value, finalEvent)
        _uiState.value = newState
        effects.forEach { effect ->
            when (effect) {
                PhoneNumberInputContract.Effect.LoadData -> handleLoadData()
                PhoneNumberInputContract.Effect.RequestSavePoint -> requestSavePoint()
                is PhoneNumberInputContract.Effect.CheckCustomerExist -> checkCustomerExist(effect.phoneNumber)
                PhoneNumberInputContract.Effect.RequestNavigateToUsePoint -> navigateToUsePoint()
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
                        val balancePoint = resource.data.toIntOrNull() ?: 0
                        val earnPoint = state.estimatedPoint
                        val config = configState.value
                        val resultState = ResultContract.State(
                            status = ResultStatus.EARN_SUCCESS,
                            title = if (earnPoint > 0) "${earnPoint.toDecimalString()}P 적립완료" else "적립완료",
                            subTitle = "${config.storeName}\n포인트가 적립되었습니다.",
                            pointTitle = "보유 포인트",
                            balancePoint = balancePoint,
                            timeOut = config.timeout,
                        )
                        dispatch(PhoneNumberInputContract.Event.OnSavePointSuccess(resultState))
                    }

                    is DataResource.Error -> dispatch(PhoneNumberInputContract.Event.OnSavePointError)
                    is DataResource.Loading -> {}
                }
            }
        }
    }

    private fun checkCustomerExist(phoneNumber: String) {
        dispatch(PhoneNumberInputContract.Event.Loading)
        val config = configState.value
        viewModelScope.launch {
            getCustomerUseCase(
                computerName = deviceInfoProvider.computerName,
                posVersion = deviceInfoProvider.posVersion,
                taxNo = config.bizNo,
                customerHp = phoneNumber,
            ).collect { resource ->
                when (resource) {
                    is DataResource.Success -> {
                        val customer = resource.data
                        dispatch(
                            PhoneNumberInputContract.Event.OnCustomerCheckResult(
                                exists = customer != null,
                                balancePoint = customer?.pointAmount?.toIntOrNull() ?: 0,
                                customerCode = customer?.customerCode ?: "",
                            )
                        )
                    }

                    is DataResource.Error -> dispatch(
                        PhoneNumberInputContract.Event.OnCustomerCheckResult(
                            exists = false,
                            balancePoint = 0,
                            customerCode = ""
                        )
                    )

                    is DataResource.Loading -> {}
                }
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
            storeName = state.storeName,
            payAmount = state.payAmount,
            balancePoint = state.balancePoint,
            minPoint = config.minPoint,
            isMinPointEnabled = config.isMinPointEnabled,
        )
        viewModelScope.launch {
            if (usePointState.isMinPointEnabled && usePointState.minPoint > usePointState.balancePoint) {
                val resultState = ResultContract.State(
                    status = ResultStatus.USE_UNAVAILABLE,
                    title = "포인트 사용 불가",
                    subTitle = "보유 포인트가 최소 사용금액(${usePointState.minPoint.toDecimalString()}P)보다 적습니다.",
                    pointTitle = "보유 포인트",
                    balancePoint = usePointState.balancePoint,
                    timeOut = config.timeout,
                )
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
                    is DataResource.Error -> {}
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
            subTitle = "${state.storeName}\n현재 보유하고 있는 포인트입니다.",
            pointTitle = "보유 포인트",
            balancePoint = state.balancePoint,
            timeOut = config.timeout,
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
