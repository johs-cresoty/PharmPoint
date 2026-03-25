package com.cresoty.catpospoint.ui.phoneNumberInput

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpospoint.presentation.phoneNumberInput.PhoneNumberInputContract
import com.cresoty.catpospoint.presentation.phoneNumberInput.PhoneNumberInputViewModel

@Composable
fun PhoneNumberInputRoute(
    vm: PhoneNumberInputViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state = vm.uiState.collectAsStateWithLifecycle()

//    LaunchedEffect(Unit) {
//        vm.effect.collect { effect ->
//            when (effect) {
//                is PhoneNumberInputContract.Effect.LoadData -> {
//                    vm.
//                }
//
//
//
//            }
//        }
//    }

    Box(Modifier.fillMaxSize()) {
        PhoneNumberInputScreen(
            state = state.value,
            sendEvent = vm::dispatch
        )
//        LoadingOverlay(isVisible = state.value.isLoading)
    }
}

