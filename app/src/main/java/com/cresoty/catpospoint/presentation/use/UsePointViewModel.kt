package com.cresoty.catpospoint.presentation.use

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cresoty.catpospoint.domain.usecase.GetConfigUseCase
import com.cresoty.catpospoint.domain.usecase.SendCATFailUseCase
import com.cresoty.catpospoint.domain.usecase.SendCATUsePointResultUseCase
import com.cresoty.catpospoint.domain.usecase.SendCATUsePointWithCustomerResultUseCase
import com.cresoty.catpospoint.domain.usecase.SendTerminalUsePointUseCase
import com.cresoty.catpospoint.model.enums.PointUseSource
import com.cresoty.catpospoint.presentation.result.ResultContract
import com.cresoty.catpospoint.presentation.result.ResultStatus
import com.cresoty.catpospoint.toDecimalString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UsePointViewModel @Inject constructor(
    private val getConfigUseCase: GetConfigUseCase,
    private val sendTerminalUsePointUseCase: SendTerminalUsePointUseCase,
    private val sendCATUsePointResultUseCase: SendCATUsePointResultUseCase,
    private val sendCATUsePointWithCustomerResultUseCase: SendCATUsePointWithCustomerResultUseCase,
    private val sendCATFailUseCase: SendCATFailUseCase,
    private val reducer: UseReducer,
) : ViewModel() {

    private val configState = getConfigUseCase()

    private val _uiState = MutableStateFlow(UseContract.State())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<UseContract.Effect>(Channel.Factory.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun dispatch(event: UseContract.Event) {
        val (newState, effects) = reducer.reduce(_uiState.value, event)
        _uiState.value = newState
        effects.forEach { effect ->
            when (effect) {
                is UseContract.Effect.SubmitUsePoint -> submitUsePoint(effect.usePoint)
                UseContract.Effect.SendCATFailAndGoToIdle -> {
                    sendCATFailUseCase()
                    viewModelScope.launch { _effect.send(UseContract.Effect.GoToIdle) }
                }
                else -> viewModelScope.launch { _effect.send(effect) }
            }
        }
    }

    private fun submitUsePoint(usePoint: Int) {
        val state = _uiState.value
        when (state.source) {
            PointUseSource.TERMINAL -> sendTerminalUsePointUseCase(
                phone   = state.phoneNumber,
                balance = state.balancePoint.toString(),
                delta   = usePoint.toString(),
            )
            PointUseSource.CAT -> sendCATUsePointResultUseCase(
                customerCode = state.customerCode,
                balance      = state.balancePoint.toString(),
                usePoint     = usePoint.toString(),
            )
            PointUseSource.CAT_WITH_CUSTOMER -> sendCATUsePointWithCustomerResultUseCase(
                usePoint = usePoint.toString(),
            )
            PointUseSource.MANUAL -> Unit
        }
        navigateToResult(usePoint)
    }

    private fun navigateToResult(usePoint: Int) {
        val state = _uiState.value
        val config = configState.value
        val subTitle = if (state.customerName.isNotEmpty()) "${state.customerName} 님" else ""
        val resultState = ResultContract.State(
            status         = ResultStatus.USE_SUCCESS,
            title          = "${usePoint.toDecimalString()}P 사용완료",
            subTitle       = subTitle,
            pointTitle     = "잔여 포인트",
            remainingPoint = (state.balancePoint - usePoint).coerceAtLeast(0),
            timeOut        = config.autoCloseTimeout,
        )
        viewModelScope.launch {
            _effect.send(UseContract.Effect.GoToResultScreen(resultState))
        }
    }
}
