package com.cresoty.catpospoint.ui.use

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpospoint.presentation.result.ResultContract
import com.cresoty.catpospoint.presentation.use.UseContract
import com.cresoty.catpospoint.presentation.use.UsePointViewModel


@Composable
fun UseRoute(
    args: UseContract.State,
    onNavigateBack: () -> Unit,
    onNavigateIdle: () -> Unit,
    onNavigateResult: (ResultContract.State) -> Unit,
    vm: UsePointViewModel = hiltViewModel(),
) {
    val state = vm.uiState.collectAsStateWithLifecycle()

    // 화면 진입 시 결과 데이터로 ViewModel 초기화
    LaunchedEffect(args) {
        vm.dispatch(UseContract.Event.Init(args))
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                UseContract.Effect.GoToIdle -> onNavigateIdle()
                UseContract.Effect.GoToBack -> onNavigateBack()
                is UseContract.Effect.GoToResultScreen -> onNavigateResult(effect.resultState)
                is UseContract.Effect.SubmitUsePoint -> Unit  // ViewModel 내부 처리, Route 불개입
                UseContract.Effect.SendCATFailAndGoToIdle -> Unit  // ViewModel 내부 처리, Route 불개입
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        UsePointScreen(
            state = state.value,
            sendEvent = vm::dispatch,
        )
    }
}
