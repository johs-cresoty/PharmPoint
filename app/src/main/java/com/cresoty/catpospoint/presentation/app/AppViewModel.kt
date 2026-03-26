package com.cresoty.catpospoint.presentation.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cresoty.catpospoint.domain.parser.TransactionDataParser
import com.cresoty.catpospoint.domain.socket.SocketEvent
import com.cresoty.catpospoint.domain.socket.SocketEventRepository
import com.cresoty.catpospoint.domain.usecase.GetConfigUseCase
import com.cresoty.catpospoint.model.enums.PaymentType
import com.cresoty.catpospoint.model.enums.PointUseSource
import com.cresoty.catpospoint.presentation.phoneNumberInput.PhoneNumberInputContract
import com.cresoty.catpospoint.presentation.result.ResultContract
import com.cresoty.catpospoint.presentation.use.UseContract
import com.cresoty.catpospoint.socket.SocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Activity 스코프 ViewModel.
 *
 * 역할:
 * 1. 소켓 이벤트를 수신 → 어느 화면으로 이동할지 결정
 * 2. [AppContract.State] 에 화면 진입 파라미터를 저장 (AppNavGraph 가 읽어 Route 에 전달)
 * 3. [AppContract.Effect] 로 화면 전환 명령 발행 → AppNavGraph 의 LaunchedEffect 에서 처리
 *
 * [MainViewModel](구 아키텍처)도 동일한 [SocketEventRepository] 를 collect 하므로
 * 전환 기간에는 두 ViewModel 이 공존할 수 있다.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val socketEventRepository: SocketEventRepository,
    private val socketManager: SocketManager,
    private val getConfigUseCase: GetConfigUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppContract.State())
    val uiState: StateFlow<AppContract.State> = _uiState.asStateFlow()

    private val _effect = Channel<AppContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            socketEventRepository.events.collect { event ->
                handleSocketEvent(event)
            }
        }
    }

    private fun handleSocketEvent(event: SocketEvent) {
        when (event) {

            // ── 단말기 전문 ─────────────────────────────────────────

            is SocketEvent.TerminalEarnPointSingle -> {
                // isAfterUse(data[7]="1"): 포인트 사용(003) 이후 적립(001) 전문 → 무시
                val isAfterUse = event.data.getOrElse(7) { "0" } == "1"
                val otc = event.data.getOrElse(5) { "0" }.toIntOrNull() ?: 0
                val config = getConfigUseCase().value
                if (isAfterUse || !config.isSave || otc == 0) return
                val td = TransactionDataParser.parseTerminalSingle(event.data)
                _uiState.update {
                    it.copy(
                        earnPointArgs = AppContract.EarnPointArgs(PointUseSource.TERMINAL, PaymentType.SINGLE, td),
                        phoneNumberInputArgs = AppContract.PhoneNumberInputArgs(
                            mode = PhoneNumberInputContract.Mode.Save(PointUseSource.TERMINAL, PaymentType.SINGLE),
                            transactionData = td,
                        )
                    )
                }
                emitEffect(AppContract.Effect.NavigateToPhoneNumberInput)
            }

            is SocketEvent.TerminalEarnPointComplex -> {
                // isAfterUse(data[7]="1"): 포인트 사용(003) 이후 적립(001) 전문 → 무시
                val isAfterUse = event.data.getOrElse(7) { "0" } == "1"
                val otc = event.data.getOrElse(5) { "0" }.toIntOrNull() ?: 0
                val config = getConfigUseCase().value
                if (isAfterUse || !config.isSave || otc == 0) return
                val td = TransactionDataParser.parseTerminalComplex(event.data)
                _uiState.update {
                    it.copy(
                        earnPointArgs = AppContract.EarnPointArgs(PointUseSource.TERMINAL, PaymentType.MULTIPLE, td),
                        phoneNumberInputArgs = AppContract.PhoneNumberInputArgs(
                            mode = PhoneNumberInputContract.Mode.Save(PointUseSource.TERMINAL, PaymentType.MULTIPLE),
                            transactionData = td,
                        )
                    )
                }
                emitEffect(AppContract.Effect.NavigateToPhoneNumberInput)
            }

            is SocketEvent.TerminalUsePoint -> {
                val td = TransactionDataParser.parseTerminalUsePoint(event.data)
                _uiState.update {
                    it.copy(
                        phoneNumberInputArgs = AppContract.PhoneNumberInputArgs(
                            mode = PhoneNumberInputContract.Mode.Lookup(PointUseSource.TERMINAL),
                            transactionData = td,
                        )
                    )
                }
                emitEffect(AppContract.Effect.NavigateToPhoneNumberInput)
            }

            // ── 캣포스 전문 ─────────────────────────────────────────

            is SocketEvent.CatConnect -> sendCatConnectAck()

            is SocketEvent.CatRequestNum -> {
                _uiState.update {
                    it.copy(
                        phoneNumberInputArgs = AppContract.PhoneNumberInputArgs(
                            mode = PhoneNumberInputContract.Mode.CatRequestNum,
                        )
                    )
                }
                emitEffect(AppContract.Effect.NavigateToCatRequestNum)
            }

            is SocketEvent.CatRequestCustomer -> {
                _uiState.update {
                    it.copy(
                        phoneNumberInputArgs = AppContract.PhoneNumberInputArgs(
                            mode = PhoneNumberInputContract.Mode.CatRequestCustomer,
                        )
                    )
                }
                emitEffect(AppContract.Effect.NavigateToCatRequestCustomer)
            }

            is SocketEvent.CatDisconnect ->
                emitEffect(AppContract.Effect.NavigateToIdle)

            is SocketEvent.CatEarnPointSingle -> {
                val td = TransactionDataParser.parseCatSingle(event.fields)
                _uiState.update {
                    it.copy(
                        earnPointArgs = AppContract.EarnPointArgs(PointUseSource.CAT, PaymentType.SINGLE, td),
                        phoneNumberInputArgs = AppContract.PhoneNumberInputArgs(
                            mode = PhoneNumberInputContract.Mode.Save(PointUseSource.CAT, PaymentType.SINGLE),
                            transactionData = td,
                        )
                    )
                }
                emitEffect(AppContract.Effect.NavigateToPhoneNumberInput)
            }

            is SocketEvent.CatEarnPointComplex -> {
                val td = TransactionDataParser.parseCatComplex(event.fields)
                _uiState.update {
                    it.copy(
                        earnPointArgs = AppContract.EarnPointArgs(PointUseSource.CAT, PaymentType.MULTIPLE, td),
                        phoneNumberInputArgs = AppContract.PhoneNumberInputArgs(
                            mode = PhoneNumberInputContract.Mode.Save(PointUseSource.CAT, PaymentType.MULTIPLE),
                            transactionData = td,
                        )
                    )
                }
                emitEffect(AppContract.Effect.NavigateToPhoneNumberInput)
            }

            is SocketEvent.CatUsePointNoCustomer -> {
                val td = TransactionDataParser.parseCatUsePoint(event.fields)
                _uiState.update {
                    it.copy(
                        phoneNumberInputArgs = AppContract.PhoneNumberInputArgs(
                            mode = PhoneNumberInputContract.Mode.Lookup(PointUseSource.CAT),
                            transactionData = td,
                        )
                    )
                }
                emitEffect(AppContract.Effect.NavigateToPhoneNumberInput)
            }

            is SocketEvent.CatUsePointWithCustomer -> {
                val config = getConfigUseCase()
                val balancePoint = event.fields.getOrElse(0) { "0" }.toIntOrNull() ?: 0
                val payAmount = event.fields.getOrElse(1) { "0" }.toIntOrNull() ?: 0
                _uiState.update {
                    it.copy(
                        usePointScreenArgs = UseContract.State(
                            source = PointUseSource.CAT_WITH_CUSTOMER,
                            storeName = config.value.storeName,
                            payAmount = payAmount,
                            balancePoint = balancePoint,
                            minPoint = config.value.minPoint,
                            isMinPointEnabled = config.value.isMinPointEnabled,
                        )
                    )
                }
                emitEffect(AppContract.Effect.NavigateToUsePoint)
            }
        }
    }

    private fun sendCatConnectAck() {
        val response = "OK|\r\n".toByteArray(Charsets.UTF_8)
        socketManager.send(response) { written ->
            Log.d("AppViewModel", "CAT 연결 응답: $written bytes")
        }
    }

    fun setPhoneNumberInputArgs(args: AppContract.PhoneNumberInputArgs) {
        _uiState.update { it.copy(phoneNumberInputArgs = args) }
    }

    fun setUsePointScreenArgs(state: UseContract.State) {
        _uiState.update { it.copy(usePointScreenArgs = state) }
    }

    fun setResultArgs(state: ResultContract.State) {
        _uiState.update { it.copy(resultArgs = state) }
    }

    private fun emitEffect(effect: AppContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
