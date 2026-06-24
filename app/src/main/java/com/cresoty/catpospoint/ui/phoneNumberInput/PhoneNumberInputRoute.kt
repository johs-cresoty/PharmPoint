package com.cresoty.catpospoint.ui.phoneNumberInput

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpospoint.presentation.app.AppContract
import com.cresoty.catpospoint.presentation.phoneNumberInput.PhoneNumberInputContract
import com.cresoty.catpospoint.presentation.phoneNumberInput.PhoneNumberInputViewModel
import com.cresoty.catpospoint.presentation.result.ResultContract
import com.cresoty.catpospoint.presentation.use.UseContract
import com.cresoty.catpospoint.ui.component.InactivityTimeoutWatcher

@Composable
fun PhoneNumberInputRoute(
    args: AppContract.PhoneNumberInputArgs,
    onNavigateBack: () -> Unit,
    onNavigateResult: (ResultContract.State) -> Unit,
    onNavigateUsePoint: (UseContract.State) -> Unit,
    vm: PhoneNumberInputViewModel = hiltViewModel(),
) {
    val state = vm.uiState.collectAsStateWithLifecycle()
    val config by vm.configState.collectAsStateWithLifecycle()

    // 화면 진입 시 모드(적립/조회)와 파싱된 거래 데이터로 ViewModel 초기화
    LaunchedEffect(args) {
        vm.dispatch(PhoneNumberInputContract.Event.Init(args.mode, args.transactionData))
    }

    // ViewModel이 비즈니스 effect를 내부 처리. Route는 네비게이션만 담당.
    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is PhoneNumberInputContract.Effect.GoToTheWaitingScreen -> onNavigateBack()
                is PhoneNumberInputContract.Effect.GoToTheResultScreen -> onNavigateResult(effect.resultState)
                is PhoneNumberInputContract.Effect.GoToUsePointScreen -> onNavigateUsePoint(effect.usePointState)
                else -> {}
            }
        }
    }

    InactivityTimeoutWatcher(
        inactivityTimeoutSeconds = config.inactiveCloseTimeout,
        onTimeout = onNavigateBack,
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(Modifier.fillMaxSize()) {
            PhoneNumberInputScreen(
                state = state.value,
                sendEvent = vm::dispatch,
            )
        }
    }
}
