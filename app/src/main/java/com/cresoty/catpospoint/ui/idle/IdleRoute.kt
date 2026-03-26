package com.cresoty.catpospoint.ui.idle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpospoint.presentation.idle.IdleContract
import com.cresoty.catpospoint.presentation.idle.IdleViewModel

@Composable
fun IdleRoute(
    onShowSettingDialog: () -> Unit = {},
    onShowPasswordDialog: () -> Unit = {},
    onNavigatePointBalance: () -> Unit = {},
    vm: IdleViewModel = hiltViewModel(),
) {
    val state = vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is IdleContract.Effect.ShowSettingDialog ->
                    if (effect.requiresPassword) onShowPasswordDialog() else onShowSettingDialog()
                IdleContract.Effect.NavigateToPointBalance -> onNavigatePointBalance()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        IdleScreen(
            state = state.value,
            sendEvent = vm::dispatch,
        )
    }
}