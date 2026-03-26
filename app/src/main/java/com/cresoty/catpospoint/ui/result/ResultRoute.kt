package com.cresoty.catpospoint.ui.result

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpospoint.presentation.result.ResultContract
import com.cresoty.catpospoint.presentation.result.ResultViewModel

@Composable
fun ResultRoute(
    args: ResultContract.State,
    onNavigateIdle: () -> Unit,
    vm: ResultViewModel = hiltViewModel(),
) {
    val state = vm.uiState.collectAsStateWithLifecycle()

    // 화면 진입 시 결과 데이터로 ViewModel 초기화
    LaunchedEffect(args) {
        vm.dispatch(ResultContract.Event.Init(args))
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                ResultContract.Effect.GoToWaiting -> onNavigateIdle()
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        ResultScreen(
            state = state.value,
            sendEvent = vm::dispatch,
        )
    }
}
