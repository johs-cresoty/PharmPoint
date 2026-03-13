package com.cresoty.catpospoint.presentation.viewmodel

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cresoty.catpospoint.BuildConfig
import com.cresoty.catpospoint.ConfigKey
import com.cresoty.catpospoint.ConfigRepository
import com.cresoty.catpospoint.PharmpayTelegram
import com.cresoty.catpospoint.Val
import com.cresoty.catpospoint.Val.CATPOS_CST
import com.cresoty.catpospoint.Val.CATPOS_DISCONNECT
import com.cresoty.catpospoint.Val.CATPOS_NUM
import com.cresoty.catpospoint.byte2String
import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.model.command.EstimatePointCommand
import com.cresoty.catpospoint.domain.model.command.PaymentDetailCommand
import com.cresoty.catpospoint.domain.model.command.UpsertCustomerPointCommand
import com.cresoty.catpospoint.domain.usecase.EstimatePointUseCase
import com.cresoty.catpospoint.domain.usecase.GetPointAmountSettingUseCase
import com.cresoty.catpospoint.domain.usecase.GetPointBalanceUseCase
import com.cresoty.catpospoint.domain.usecase.GetPointSaveSettingUseCase
import com.cresoty.catpospoint.domain.usecase.IsCustomersUseCase
import com.cresoty.catpospoint.domain.usecase.UpsertCustomerPointUseCase
import com.cresoty.catpospoint.model.enums.PointDeltaProcess
import com.cresoty.catpospoint.model.enums.PointQuickInputType
import com.cresoty.catpospoint.model.interfaces.Dialogs
import com.cresoty.catpospoint.model.interfaces.PadAction
import com.cresoty.catpospoint.model.state.CustomerState
import com.cresoty.catpospoint.model.state.MainState
import com.cresoty.catpospoint.model.state.PointState
import com.cresoty.catpospoint.model.state.PreviewState
import com.cresoty.catpospoint.model.state.SettingState
import com.cresoty.catpospoint.safeSubString
import com.cresoty.catpospoint.socket.SocketManager
import com.cresoty.catpospoint.socket.protocol.CatposMessage
import com.cresoty.catpospoint.splitTelegram
import com.cresoty.catpospoint.toIntOrMax
import com.cresoty.catpospoint.view.composable.list.SettingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val configRepo: ConfigRepository,
    private val socketManager: SocketManager,
    private val isCustomersUseCase: IsCustomersUseCase,
    private val upsertCustomerPointUseCase: UpsertCustomerPointUseCase,
    private val estimatePointUseCase: EstimatePointUseCase,
    private val getPointSaveSettingUseCase: GetPointSaveSettingUseCase,
    private val getPointAmountSettingUseCase: GetPointAmountSettingUseCase,
    private val getPointBalanceUseCase: GetPointBalanceUseCase,
) : ViewModel() {

    private val CMPTR_NAME = "${Build.BRAND}_${Build.MODEL}"
    private val POS_VER = BuildConfig.VERSION_NAME

    // mainState
    private val _pointDeltaStep: MutableStateFlow<PointDeltaProcess> =
        MutableStateFlow(PointDeltaProcess.NONE)
    private val _paymentAmount: MutableStateFlow<String> = MutableStateFlow("")

    // pointState
    private val _pointDelta: MutableStateFlow<String> = MutableStateFlow("")
    private val _pointBalance: MutableStateFlow<String> = MutableStateFlow("")
    private val _isPersonalInfoUse: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val _isMasking: MutableStateFlow<Boolean> = MutableStateFlow(true)

    // settingState
    private val _dialog: MutableStateFlow<Dialogs> = MutableStateFlow(Dialogs.None)
    private val _selectedMenuIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    private val _isPasswordCorrect: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val _password: MutableStateFlow<String> = MutableStateFlow("")
    private val _isSavedToastVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)

    // previewState
    private val _isHideDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _preSubTitle: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _preTheme: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _preCustomImageUri: MutableStateFlow<Uri?> = MutableStateFlow(null)

    // 사용자 지정 테마 이미지 (설정 다이얼로그 닫혀도 유지)
    private val _customThemeImageUri: MutableStateFlow<Uri?> = MutableStateFlow(null)
    val customThemeImageUriState: StateFlow<Uri?> = _customThemeImageUri

    //customerState
    private val _isExistChecking: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _isExist: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _verifyResult: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    private val _verifyNumber: MutableStateFlow<String> = MutableStateFlow("")
    private val _phoneNumber: MutableStateFlow<String> = MutableStateFlow("010")

    //포인트 적립
    private var approvalNumber: String = ""

    // 타입 변경
    private var complexTranInfo: Pair<PaymentDetailCommand, PaymentDetailCommand>? = null

    //    private var transactionAmount : String = ""
    private var transactionDate: String = ""
    private var transactionTime: String = ""
    private var transactionMethod: String = ""
    private var transactionUniqueNumber: String = ""   //거래고유번호

    val configState = configRepo.configState

    val customerState: StateFlow<CustomerState> = combine(
        _isExist,
        _isExistChecking,
        _verifyResult,
        _verifyNumber,
        _phoneNumber,
    ) { isExist, isChecking, verifyResult, verifyNumber, phoneNumber ->
        CustomerState(
            phoneNumber = phoneNumber,
            isCustomerExist = isExist,
            isExistChecking = isChecking,
            verifyResult = verifyResult,
            verifyNumber = verifyNumber
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.Eagerly,
        initialValue = CustomerState()
    )

    val previewState: StateFlow<PreviewState> = combine(
        _isHideDialog,
        _preSubTitle,
        _preTheme,
        _preCustomImageUri
    ) { isHideDialog, preSubTitle, preTheme, preCustomImageUri ->
        PreviewState(
            isHideDialog = isHideDialog,
            subTitle = preSubTitle,
            theme = preTheme,
            customImageUri = preCustomImageUri
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5_000),
        initialValue = PreviewState()
    )

    val settingState: StateFlow<SettingState> = combine(
        _dialog,
        _selectedMenuIndex,
        _isPasswordCorrect,
        _password,
        _isSavedToastVisible,
    ) { dialog, selectedMenuIndex, isPasswordCorrect, inputNumber, isSavedToastVisible ->
        SettingState(
            dialog = dialog,
            selectedIndex = selectedMenuIndex,
            isPasswordCorrect = isPasswordCorrect,
            password = inputNumber,
            isSavedToastVisible = isSavedToastVisible,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5_000),
        initialValue = SettingState()
    )

    val pointState: StateFlow<PointState> = combine(
        _pointDelta,
        _pointBalance,
        _isPersonalInfoUse,
        _isMasking
    ) { pointDelta, pointBalance, isPersonalInfoUse, isMasking ->
        PointState(
            pointDelta = pointDelta,
            pointBalance = pointBalance,
            isPersonalInfoUse = isPersonalInfoUse,
            isMasking = isMasking
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5_000),
        initialValue = PointState()
    )

    val mainState: StateFlow<MainState> = combine(
        _pointDeltaStep,
        _paymentAmount, //
    ) { pointDeltaStep, paymentAmount ->
        MainState(
            pointDeltaStep = pointDeltaStep,
            paymentAmount = paymentAmount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5_000),
        initialValue = MainState()
    )

    private var socketJob: Job? = null


    init {
        Log.d("SocketDebug", "SocketManager.start() 호출")
        socketJob = socketManager.start()

        socketManager.telegramReceiver = { cmd, data ->
            Log.d("SocketDebug", "received telegram : ${data.byte2String()}")
            processTerminalTelegram(cmd, data)
        }

        socketManager.pcTelegramReceiver = { msg ->
            Log.d("SocketDebug", "received catpos msg : $msg")
            processPcTelegram(msg)
        }

        initRequestPointSettings()

        // 저장된 사용자 지정 이미지 URI 복원
        viewModelScope.launch {
            val savedUri = configRepo.getValue(ConfigKey.CUSTOM_IMAGE_URI, "")
            if (savedUri.isNotEmpty()) {
                _customThemeImageUri.update { Uri.parse(savedUri) }
            }
        }
    }

    /**
     * 클릭 이벤트 등 action
     *
     * @param action
     */
    fun dispatch(action: PadAction) {
        when (action) {
            is PadAction.OnClickSettingMenu -> updateMenuIndex(action.index)
            is PadAction.OnClickSaveSetting -> saveSettingOption(action.type, action.data)
            is PadAction.OnClickPasswordPad -> updatePassword(action.number)
            is PadAction.OnClickAdminLogin -> passwordVerify(action.input)
            is PadAction.OnClickShowPreview -> showPreview(action.preIndex, action.subTitle, action.customImageUri)
            is PadAction.OnCustomThemeImageCropped -> {
                val newUri = action.uri.takeIf { it != Uri.EMPTY }
                _customThemeImageUri.value?.path?.let { java.io.File(it).delete() }
                _customThemeImageUri.update { newUri }
            }
            is PadAction.OnClickNumberPad -> updateInputNumber(action.number)
            is PadAction.OnClickPointNext -> updatePointDeltaStep(action.next)
            is PadAction.OnClickAmountQuickButton -> updatePointUseAmountQuick(action.type)

            PadAction.OnClickSetting -> updateDialogSetting()
            PadAction.CloseDialog -> updateDialog(Dialogs.None)
            PadAction.OnClickDeleteLastPassword -> deletePasswordLast()
            PadAction.OnClickDeleteAllPassword -> deleteAllPassword()
            PadAction.OnClickClosePreview -> closePreview()
            PadAction.OnClickDeleteAllPhoneNumber -> deleteAllPhoneNumber()
            PadAction.OnClickDeleteLastPhoneNumber -> deleteNumberLast()
            PadAction.OnClickPersonalInfoUse -> updatePersonalInfoUse()
            PadAction.OnClickMaskingToggle -> updateIsMasking()

            PadAction.RequestSavePoint -> requestSavePoint()
            PadAction.RequestPointBalanceCheck -> requestPointBalanceCheck()
            PadAction.RequestCustomerVerify -> {}
            PadAction.SendToTerminalPointUse -> sendToTerminalUsePoint()
            PadAction.RequestExpectSaveAmount -> checkExpectPointAmount(emptyList())
            PadAction.SendToCATCustomerInfo -> sendToCATCustomerInfo()
            PadAction.SendToCATPhoneNumber -> sendToCATPhoneNumber()
            PadAction.SendCATFail -> sendCATFail()
        }
    }

    /**
     * 단말기로부터 수신한 전문 파싱 및 수신한 전문 종류(cmd)에 따라 프로세스 진행
     * 001 : 적립요청
     * 002 : 적립요청(복합결제)
     * 003 : 사용요청
     * CAT : 고객 조회(없을 경우 신규 가입)
     *
     * @param cmd
     * @param data
     */
    private fun processTerminalTelegram(
        cmd: String,
        data: ByteArray
    ) {

        val split = data.splitTelegram(Val.COMM_FS)
        val list = split.map { it.byte2String() }

        when (cmd) {
            Val.TERMINAL_COMMAND_001 -> {
                checkExpectPointAmount(list)
            }

            Val.TERMINAL_COMMAND_002 -> {
                checkExpectPointAmountComplex(list)
            }

            Val.TERMINAL_COMMAND_003 -> {
                processPointUse(list)
            }
        }
    }

    private fun processPcTelegram(msg: CatposMessage) {
        when (msg.command) {
            CATPOS_NUM -> {
                updatePointDeltaStep(PointDeltaProcess.REQUEST_NUM)
            }

            CATPOS_CST -> {
                updatePointDeltaStep(PointDeltaProcess.REQUEST_CST)
            }
            CATPOS_DISCONNECT ->{
                updatePointDeltaStep(PointDeltaProcess.NONE, sendInit = false)
            }

            else -> {
                Log.d("@#@#", "Unknown PC command: ${msg.command}")
            }
        }
    }


    private fun sendToCATCustomerInfo() {
        viewModelScope.launch {
            val phone = customerState.value.phoneNumber
            getPointBalanceUseCase(
                taxNo = configState.value.bizNo,
                customerPhone = phone
            ).collect { resource ->
                when (resource) {
                    is DataResource.Success -> {
                        Log.d("SocketDebug", "sendToCATCustomerInfo 응답:${resource.data}")
                        val responseBytes =
                            ("OK|${resource.data.customerPhone}|${resource.data.customerCode}\r\n")
                                .toByteArray(Charsets.UTF_8)
                        socketManager.send(responseBytes) { written ->
                            Log.d(
                                "SocketDebug",
                                "PC로 텍스트 전송 완료: $written bytes, 내용: 'OK|${resource.data.customerPhone}|${resource.data.customerCode}'"
                            )
                        }

                        updatePointDeltaStep(PointDeltaProcess.NONE, sendInit = false)
                    }

                    is DataResource.Error -> {
                        Log.d("SocketDebug", "getPointBalance error: ${resource.throwable.message}")
                    }

                    is DataResource.Loading -> {}
                }
            }
        }
    }

    private fun sendToCATPhoneNumber() {
        val phone = customerState.value.phoneNumber
        val responseBytes = "OK|$phone\r\n".toByteArray(Charsets.UTF_8)
        socketManager.send(responseBytes) { written ->
            Log.d(
                "SocketDebug",
                "캣포스로 텍스트 전송 완료: $written bytes, 내용: '$responseBytes'"
            )
        }

        updatePointDeltaStep(PointDeltaProcess.NONE, sendInit = false)

    }

    private fun sendCATFail() {
        val failBytes = "FAIL|100|다음에하기\r\n".toByteArray(Charsets.UTF_8)
        socketManager.send(failBytes)
        updatePointDeltaStep(PointDeltaProcess.NONE, sendInit = false)
    }

    /**
     * 단말기로 포인트 사용요청 전문(003)의 응답전문(004) 전송
     *
     */
    private fun sendToTerminalUsePoint() {
        viewModelScope.launch(Dispatchers.IO) {
            val buff = PharmpayTelegram.makeUsePoint(
                phone = _phoneNumber.value,
                balance = _pointBalance.value,
                delta = _pointDelta.value
            )

            socketManager.send(buff)

            withContext(Dispatchers.Main) {
                val current = _pointBalance.value.toIntOrNull() ?: 0
                val useAmount = _pointDelta.value.toIntOrNull() ?: 0
                _pointBalance.update {
                    (current - useAmount).toString()
                }
                updatePointDeltaStep(PointDeltaProcess.POINT_USE_PROC_DONE)
            }
        }
    }

    /**
     * 단말기에서 포인트 적립 전문(002) 수신 후
     * 적립 프로세스 시작
     * 복합결제 예상 적립 포인트 조회
     *
     * @param list
     */
    private fun checkExpectPointAmountComplex(list: List<String>) {
        val date = list[1].safeSubString(0, 8)
        val time = list[1].safeSubString(8)
        val firstOtc = list[5].toIntOrNull() ?: 0
        val firstVat = list[6].toIntOrNull() ?: 0
        val secondOtc = list[10].toIntOrNull() ?: 0
        val secondVat = list[11].toIntOrNull() ?: 0

        val firstMethod = if (list[3] == "P") "M" else list[3]
        val secondMethod = if (list[8] == "P") "M" else list[3]

        val first = PaymentDetailCommand(
            approvalNumber = list[2],
            transactionGubn = firstMethod,
            transactionDate = date,
            transactionTime = time,
            transactionAmount = (firstOtc + firstVat).toString()
        )

        val second = PaymentDetailCommand(
            approvalNumber = list[7],
            transactionGubn = secondMethod,
            transactionDate = date,
            transactionTime = time,
            transactionAmount = (secondOtc + secondVat).toString()
        )

//        if (!complexPaymentAmountCheck(first to second)) return

        complexTranInfo = first to second
        transactionMethod = first.transactionGubn
        transactionDate = date

        _paymentAmount.update {
            (firstOtc + firstVat + secondOtc + secondVat).toString()
        }

        viewModelScope.launch {
            estimatePointUseCase(
                EstimatePointCommand.Complex(
                    taxNo = configState.value.bizNo,
                    computerName = CMPTR_NAME,
                    posVersion = POS_VER,
                    payments = first to second
                )
            ).collect { resource ->
                when (resource) {
                    is DataResource.Success -> {
                        resource.data?.let {
                            _pointDelta.update { _ -> it.pointAmount }
                            transactionUniqueNumber = it.sleSeq
                        }
                        updatePointDeltaStep(PointDeltaProcess.POINT_SAVE_PHONE_NUM)
                    }

                    is DataResource.Error -> {
                        Log.d("jhs", "복합 estimatePoint error: ${resource.throwable.message}")
                        updatePointDeltaStep(PointDeltaProcess.POINT_SAVE_PHONE_NUM)
                    }

                    is DataResource.Loading -> {}
                }
            }
        }
    }

    /**
     * 복합결제 결제금액 체크
     * 현금수납 외 모든 결제수단은 카드결제로 인식(이하 기타수단)
     * 현금수납과 기타수단은 각각 클라우드에서 설정한 최저금액보다 높아야 함
     * 최저금액 설정이 1000원이라고 가정하면
     * ex1) 현금수납 1100원, 기타수단 500원   -> 현금수납 1100원에 대한 포인트만 적립
     * ex2) 기타수단 600원, 기타수단 500원    -> 기타수단 1100원에 대한 포인트 적립
     * ex3) 현금수납 500원, 현금수납 600원    -> 현금수납 1100원에 대한 포인트 적립
     * ex4) 현금수납 400원, 기타수단 500원    -> 각자 최저금액을 넘기지 못했으므로 적립X
     *
     * @param pair
     * @return
     */
// complexPaymentAmountCheck도 타입 변경
    private fun complexPaymentAmountCheck(pair: Pair<PaymentDetailCommand, PaymentDetailCommand>): Boolean {
        val isFirstCashAccept = pair.first.transactionGubn == "M"
        val isSecondCashAccept = pair.second.transactionGubn == "M"
        val firstAmount = pair.first.transactionAmount.toIntOrNull() ?: 0  // toIntOrNull() 추가
        val secondAmount = pair.second.transactionAmount.toIntOrNull() ?: 0  // toIntOrNull() 추가
        val min = configState.value.minAmount

        return when {
            isFirstCashAccept && isSecondCashAccept -> firstAmount + secondAmount > min //// 모두 현금수납 : 합산금액이 최저금액보다 높아야
            isFirstCashAccept || isSecondCashAccept -> firstAmount > min || secondAmount > min // 둘 중 하나만 현금수납 : 각각 최저금액보다 높아야
            else -> firstAmount + secondAmount > min  // 모두 현금수납X : 합산금액이 최저금액보다 높아야
        }
    }

    /**
     * 단말기에서 포인트 적립 전문(001) 수신 후
     * 적립 프로세스 시작
     * 예상 적립 포인트 조회
     *
     * @param list
     */
    private fun checkExpectPointAmount(list: List<String>) {
        val date = list[1].safeSubString(0, 8)
        val time = list[1].safeSubString(8)
        val appnum = list[2]
        val method = if (list[3] == "P") "M" else list[3]

//        val etc = list[4].toIntOrNull() ?: 0
        val otc = list[5].toIntOrNull() ?: 0
        val vat = list[6].toIntOrNull() ?: 0
        val isAfterUse = list[7] == "1"

        val total = (otc + vat)
        val min = configState.value.minPoint
        val minAmount = configState.value.minAmount
        val isSave = configState.value.isSave
        Log.d(
            "SocketDebug",
            "total=$total, min=${configState.value.minPoint}, isSave=${configState.value.isSave}, minAmount =$minAmount"
        )
        if (isAfterUse) return
        if (!isSave) return
//        if (total <= min) return
//        if (total <= minAmount) return
        if (otc == 0) return

        approvalNumber = appnum
        transactionMethod = method
        transactionDate = date
        transactionTime = time

        _paymentAmount.update {
            total.toString()
        }

        updatePointDeltaStep(PointDeltaProcess.POINT_SAVE_PHONE_NUM)

        viewModelScope.launch {
            estimatePointUseCase(
                EstimatePointCommand.Single(
                    taxNo = configState.value.bizNo,
                    computerName = CMPTR_NAME,
                    posVersion = POS_VER,
                    trnDate = transactionDate,
                    trnGubn = transactionMethod,
                    trnAmt = _paymentAmount.value,
                    appNum = approvalNumber
                )
            ).collect { resource ->
                when (resource) {
                    is DataResource.Success -> {
                        resource.data?.let {
                            _pointDelta.update { _ -> it.pointAmount }
                            transactionUniqueNumber = it.sleSeq
                        }
                    }

                    is DataResource.Error -> {
                        Log.d("jhs", "estimatePoint error: ${resource.throwable.message}")
                    }

                    is DataResource.Loading -> {}
                }
            }
        }
    }

    /**
     * 입력된 번호로 포인트 적립 요청
     *
     */
    private fun requestSavePoint() {
        viewModelScope.launch {
            val phone = customerState.value.phoneNumber
            val bizNo = configState.value.bizNo


            val command = complexTranInfo?.let { pair ->
                if (transactionUniqueNumber.isNotEmpty()) {
                    // 복합결제 sle_seq 있을 때 → BySleSeq
                    UpsertCustomerPointCommand.BySleSeq(
                        taxNo = bizNo,
                        computerName = CMPTR_NAME,
                        posVersion = POS_VER,
                        customerPhone = phone,
                        transactionDate = transactionDate,
                        sleSeq = transactionUniqueNumber
                    )
                } else {
                    // 복합결제 sle_seq 없을 때 → ByMultiplePayment
                    UpsertCustomerPointCommand.ByMultiplePayment(
                        taxNo = bizNo,
                        computerName = CMPTR_NAME,
                        posVersion = POS_VER,
                        customerPhone = phone,
                        transactionDate = transactionDate,
                        transactionAmount = _paymentAmount.value,
                        payments = listOf(pair.first, pair.second)
                    )
                }
            } ?: if (transactionUniqueNumber.isNotEmpty()) {
                // 단일결제 sle_seq 있을 때 → BySleSeq
                UpsertCustomerPointCommand.BySleSeq(
                    taxNo = bizNo,
                    computerName = CMPTR_NAME,
                    posVersion = POS_VER,
                    customerPhone = phone,
                    transactionDate = transactionDate,
                    sleSeq = transactionUniqueNumber
                )
            } else {
                // 단일결제 sle_seq 없을 때 → BySinglePayment
                UpsertCustomerPointCommand.BySinglePayment(
                    taxNo = bizNo,
                    computerName = CMPTR_NAME,
                    posVersion = POS_VER,
                    customerPhone = phone,
                    transactionDate = transactionDate,
                    transactionGubn = transactionMethod,
                    transactionTime = transactionTime,
                    transactionAmount = _paymentAmount.value,
                    approvalNumber = approvalNumber
                )
            }

            upsertCustomerPointUseCase(command).collect { resource ->
                when (resource) {
                    is DataResource.Success -> {
                        _pointBalance.update { resource.data ?: "0" }
                        updatePointDeltaStep(PointDeltaProcess.POINT_SAVE_PROC_DONE)
                    }

                    is DataResource.Error -> {
                        Log.d("jhs", "에러: ${resource.throwable.message}")
                        // 기존 NetworkManager의 safeNetworkCall이 delegate?.onNetworkError 호출하던 부분
                        // 필요하다면 에러 state 추가
                    }

                    is DataResource.Loading -> {}
                }
            }
        }
    }

    /**
     * 포인트 사용 전 포인트 잔액 조회
     *
     */
    private fun requestPointBalanceCheck() {
        viewModelScope.launch {
            val phone = customerState.value.phoneNumber
            getPointBalanceUseCase(
                taxNo = configState.value.bizNo,
                customerPhone = phone
            ).collect { resource ->
                when (resource) {
                    is DataResource.Success -> {
                        val balance = resource.data.pointBalance
                        _pointBalance.update { balance }
                        val balanceAmount = balance.toIntOrNull() ?: 0
                        val isShortage = balanceAmount == 0 ||
                                (configState.value.isMinPointEnabled && balanceAmount < configState.value.minPoint)
                        if (isShortage) {
                            updatePointDeltaStep(PointDeltaProcess.POINT_USE_PROC_SHORTAGE_FAIL)
                        } else {
                            val isVerify = configState.value.isIdVerify
                            if (isVerify) updatePointDeltaStep(PointDeltaProcess.POINT_USE_VERIFY_NUM)
                            else updatePointDeltaStep(PointDeltaProcess.POINT_USE_AMOUNT_INPUT)
                        }
                    }

                    is DataResource.Error -> {
                        Log.d("jhs", "getPointBalance error: ${resource.throwable.message}")
                    }

                    is DataResource.Loading -> {}
                }
            }
        }
    }

    private fun initRequestPointSettings() {
        val taxNo = configState.value.bizNo
        viewModelScope.launch {
            getPointSaveSettingUseCase(taxNo).collect { resource ->
                if (resource is DataResource.Success) {
                    configRepo.putValue(ConfigKey.IS_SAVE, resource.data.isSave)
                }
            }
        }
        viewModelScope.launch {
            getPointAmountSettingUseCase(taxNo).collect { resource ->
                if (resource is DataResource.Success) {
                    configRepo.putValue(ConfigKey.MIN_AMOUNT, resource.data.minAmount)
                }
            }
        }
    }

    /**
     * 포인트 사용 전문 수신 후 프로세스 시작
     *
     * @param list[1] : 사업자번호
     */
    private fun processPointUse(list: List<String>) {
        if (configState.value.bizNo == list[1]) {
            val etc = list[2].toIntOrNull() ?: 0
            val otc = list[3].toIntOrNull() ?: 0
            val vat = list[4].toIntOrNull() ?: 0

            _paymentAmount.update {
                (otc + vat).toString()
            }

            updatePointDeltaStep(PointDeltaProcess.POINT_USE_PHONE_NUM)
        }
    }

    /**
     * 세팅 다이얼로그에서 저장하는 설정값
     *
     * @param type : 현재 선택된 다이얼로그
     * @param data : 저장 키 및 저장 값
     */
    private fun saveSettingOption(type: SettingType, data: Map<Preferences.Key<*>, Any>) {
        viewModelScope.launch {
            when (type) {
                SettingType.STORE_INFO -> {
                    val bizNo = data[ConfigKey.BIZ_NO] as String
                    val storeName = data[ConfigKey.STORE_NAME] as String

                    configRepo.putValue(ConfigKey.BIZ_NO, bizNo)
                    configRepo.putValue(ConfigKey.STORE_NAME, storeName)
                }
//                SettingType.ID_VERIFY -> {
//                    val isIdVerify = data[ConfigKey.IS_ID_VERIFY]  as Boolean
//
//                    configRepo.putValue(ConfigKey.IS_ID_VERIFY, isIdVerify)
//                }
                SettingType.POINT_USE -> {
                    val isPointUse = data[ConfigKey.IS_MIN_POINT_ENABLED] as Boolean
                    val minPoint = data[ConfigKey.MINIMUM_POINT] as Int

                    configRepo.putValue(ConfigKey.IS_MIN_POINT_ENABLED, isPointUse)
                    configRepo.putValue(ConfigKey.MINIMUM_POINT, minPoint)
                }

                SettingType.SCREEN_TIMEOUT -> {
                    val timeout = data[ConfigKey.SCREEN_TIMEOUT] as Int

                    configRepo.putValue(ConfigKey.SCREEN_TIMEOUT, timeout)
                }

                SettingType.THEME -> {
                    val themeIndex = data[ConfigKey.MAIN_THEME] as Int
                    val subTitle = data[ConfigKey.SUB_TITLE] as String
                    val customUri = _customThemeImageUri.value
                        ?.takeIf { it != Uri.EMPTY }
                        ?.toString() ?: ""

                    configRepo.putValue(ConfigKey.MAIN_THEME, themeIndex)
                    configRepo.putValue(ConfigKey.SUB_TITLE, subTitle)
                    configRepo.putValue(ConfigKey.CUSTOM_IMAGE_URI, customUri)
                }
            }

            _isSavedToastVisible.update { true }
            kotlinx.coroutines.delay(2000)
            _isSavedToastVisible.update { false }
        }
    }

    /**
     * 적립/사용 프로세스 종료 후 값 초기화
     *
     */
    private fun initPointState() {
        _pointBalance.update { "" }
        _pointDelta.update { "" }
        _isMasking.update { true }
        _isPersonalInfoUse.update { true }
        _phoneNumber.update { "010" }
        _paymentAmount.update { "" }
        _isExist.update { false }

        approvalNumber = ""
        transactionMethod = ""
        transactionDate = ""
        transactionTime = ""
        transactionUniqueNumber = ""

        complexTranInfo = null
    }

    /**
     * 포인트 적립/사용 프로세스 진행
     * 적립 : 번호입력 -> 적립완료
     * 사용 : 번호입력 -> (옵션)인증번호입력 -> 사용포인트입력 -> 사용완료
     *
     * @param step
     */
    private fun updatePointDeltaStep(step: PointDeltaProcess, sendInit: Boolean = true) {

        if (step == PointDeltaProcess.NONE) {
            if (sendInit) {
                val buff = PharmpayTelegram.makeInit()
                socketManager.send(buff)
            }
            initPointState()
        }

        if (step == PointDeltaProcess.POINT_SAVE_PHONE_NUM ||
            step == PointDeltaProcess.POINT_USE_PHONE_NUM ||
            step == PointDeltaProcess.REQUEST_CST ||
            step == PointDeltaProcess.REQUEST_NUM
        ) {
            _phoneNumber.update { "010" }
        }

        _pointDeltaStep.update {
            step
        }
    }

    /**
     * 전화번호 마스킹 여부 update
     *
     */
    private fun updateIsMasking() {
        _isMasking.update {
            !(_isMasking.value)
        }
    }

    /**
     * 개인정보 제공 동의 여부 update
     *
     */
    private fun updatePersonalInfoUse() {
        _isPersonalInfoUse.update {
            !(_isPersonalInfoUse.value)
        }
    }

    /**
     * 사용시 포인트 간편입력버튼(+100 / +1000 / 전액)
     * 보유 포인트 총액 초과시 총액으로 제한
     *
     * @param input : 100 / 1000 / 전액
     */
    private fun updatePointUseAmountQuick(input: PointQuickInputType) {
        val payAmount = _paymentAmount.value.toIntOrMax()
        val balance = _pointBalance.value.toIntOrMax()
        val current = _pointDelta.value.toIntOrMax()
        val max = minOf(payAmount, balance)

        val result = when (input) {
            PointQuickInputType.PLUS_ALL -> max
            else -> (current + input.amount.toInt()).coerceAtMost(max)
        }.coerceAtMost(max)

        _pointDelta.update { result.toString() }
    }

    /**
     * NumberPad Composable 키패드 클릭 이벤트 수신
     *
     * @param input
     */
    private fun updateInputNumber(input: String) {
        val step = _pointDeltaStep.value
        when (step) {
            PointDeltaProcess.POINT_SAVE_PHONE_NUM,
            PointDeltaProcess.POINT_USE_PHONE_NUM,
            PointDeltaProcess.REQUEST_CST,
            PointDeltaProcess.REQUEST_NUM -> {
                var current = _phoneNumber.value
                if (current.length <= 10) current += input

                _phoneNumber.update { current }

                if ((step == PointDeltaProcess.POINT_USE_PHONE_NUM || step == PointDeltaProcess.REQUEST_CST) && current.length == 11) {
                    checkCustomerExist(current)
                }
            }

            PointDeltaProcess.POINT_USE_VERIFY_NUM -> {
                var current = _verifyNumber.value
                if (current.length <= 5) current += input

                _verifyNumber.update { current }
            }

            PointDeltaProcess.POINT_USE_AMOUNT_INPUT -> {
                var current = _pointDelta.value
                val payAmount = _paymentAmount.value.toIntOrMax()
                val balance = _pointBalance.value.toIntOrMax()
                val max = minOf(payAmount, balance)

                current += input

                if ((current.toIntOrNull() ?: 0) > max) {
                    current = max.toString()
                }
                _pointDelta.update {
                    current
                }
            }

            else -> return
        }
    }

    private fun checkCustomerExist(phone: String) {
        viewModelScope.launch {
            _isExistChecking.update { true }

            isCustomersUseCase(
                computerName = "POS",
                posVersion = BuildConfig.VERSION_NAME,
                taxNo = configState.value.bizNo,
                customerHp = phone
            ).collect { resource ->
                when (resource) {
                    is DataResource.Success -> {
                        _isExist.update { resource.data }
                        _isExistChecking.update { false }
                    }

                    is DataResource.Error -> {
                        _isExist.update { false }
                        _isExistChecking.update { false }
                    }

                    is DataResource.Loading -> {}
                }
            }
        }
    }

    /**
     * 전화번호 입력 끝자리 삭제
     *
     */
    private fun deleteNumberLast() {
        val step = _pointDeltaStep.value
        when (step) {
            PointDeltaProcess.POINT_SAVE_PHONE_NUM,
            PointDeltaProcess.POINT_USE_PHONE_NUM,
            PointDeltaProcess.REQUEST_CST,
            PointDeltaProcess.REQUEST_NUM -> {
                val current = _phoneNumber.value
                _phoneNumber.update {
                    current.dropLast(1)
                }
            }

            PointDeltaProcess.POINT_USE_VERIFY_NUM -> {
                val current = _verifyNumber.value
                _verifyNumber.update {
                    current.dropLast(1)
                }
            }

            PointDeltaProcess.POINT_USE_AMOUNT_INPUT -> {
                val current = _pointDelta.value
                _pointDelta.update {
                    current.dropLast(1)
                }
            }

            else -> return
        }

    }

    /**
     * 전화번호 입력 전체 삭제
     *
     */
    private fun deleteAllPhoneNumber() {
        val step = _pointDeltaStep.value
        when (step) {
            PointDeltaProcess.POINT_SAVE_PHONE_NUM,
            PointDeltaProcess.POINT_USE_PHONE_NUM,
            PointDeltaProcess.REQUEST_CST,
            PointDeltaProcess.REQUEST_NUM -> {
                _phoneNumber.update {
                    ""
                }
            }

            PointDeltaProcess.POINT_USE_VERIFY_NUM -> {
                _verifyNumber.update {
                    ""
                }
            }

            PointDeltaProcess.POINT_USE_AMOUNT_INPUT -> {
                _pointDelta.update {
                    ""
                }
            }

            else -> return
        }
    }

    /**
     * 설정화면 테마 미리보기 update
     *
     * _preTheme : 미리보기 테마
     * _preSubTitle : 미리보기 서브타이틀
     *
     * @param index
     * @param subTitle
     */
    private fun showPreview(index: Int, subTitle: String, customImageUri: Uri? = null) {
//        _dialog.update { Dialogs.None }

        _isHideDialog.update { true }
        _preTheme.update { index }
        _preSubTitle.update { subTitle }
        _preCustomImageUri.update { customImageUri }
    }

    /**
     * 미리보기 닫기
     *
     */
    private fun closePreview() {
        _dialog.update { Dialogs.Setting }

        _isHideDialog.update { false }
        _preTheme.update { null }
        _preSubTitle.update { null }
        _preCustomImageUri.update { null }

    }

    /**
     * 설정 진입시 비밀번호 입력
     *
     * @param number
     */
    private fun updatePassword(number: String) {
        var current = _password.value

        if (current.length <= 4) current += number

        _password.update {
            current
        }
    }

    /**
     * 비밀번호 전체삭제
     *
     */
    private fun deleteAllPassword() {
        _password.update {
            ""
        }
    }

    /**
     * 비밀번호 끝자리 삭제
     *
     */
    private fun deletePasswordLast() {
        val current = _password.value
        _password.update {
            current.dropLast(1)
        }
    }

    /**
     * 비밀번호 끝자리 사업자번호 뒤 5자리와 일치여부 판단
     *
     * @param input : 입력한 비밀번호
     */
    private fun passwordVerify(input: String) {
        viewModelScope.launch {
            val password = configState.value.bizNo.safeSubString(5)
            if (password == input) {
                updateDialog(Dialogs.Setting)
                updatePassword("")
                updatePasswordCorrect(true)
            } else updatePasswordCorrect(false)
        }
    }

    /**
     * 비밀번호 일치여부 update
     * 불일치시 비밀번호 dialog에 불일치 경고문구 표출
     *
     * @param result
     */
    private fun updatePasswordCorrect(result: Boolean) {
        _isPasswordCorrect.update {
            result
        }
    }

    /**
     * 사업자번호 설정값이 비어있지 않을 경우 비밀번호 입력창 표출
     * 비어있을 경우 바로 설정 dialog 표출
     * (개발 편의상 DEBUG모드일 경우 비밀번호 입력창 skip)
     *
     */
    private fun updateDialogSetting() {
        viewModelScope.launch {
//            if (BuildConfig.DEBUG) {
//                _dialog.update {
//                    Dialogs.Setting
//                }
//            } else {
            val bizNo = configState.value.bizNo.isEmpty()
            val dialog: Dialogs = if (bizNo) Dialogs.Setting
            else Dialogs.InputPassword

            _dialog.update {
                dialog
            }
//            }
        }
    }

    /**
     * dialog 상태값 update
     * (현재는 Setting, PasswordInput, None 세가지 상태 뿐이지만 추후 필요시 추가)
     * dialog창 닫을 시 입력한 비밀번호, 선택한 index 초기화
     *
     * @param input
     */
    private fun updateDialog(input: Dialogs) {
        if (input == Dialogs.None) {
            deleteAllPassword()
            updateMenuIndex(0)
            updatePasswordCorrect(true)
        }

        _dialog.update {
            input
        }
    }

    /**
     * 설정창 메뉴 선택 index update
     *
     * @param index
     */
    private fun updateMenuIndex(index: Int) {
        _selectedMenuIndex.update {
            index
        }
    }

}