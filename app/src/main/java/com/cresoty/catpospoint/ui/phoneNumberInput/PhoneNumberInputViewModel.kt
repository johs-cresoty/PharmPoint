package com.cresoty.catpospoint.ui.phoneNumberInput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cresoty.catpospoint.domain.usecase.EstimatePointUseCase
import com.cresoty.catpospoint.domain.usecase.GetConfigUseCase
import com.cresoty.catpospoint.presentation.phoneNumberInput.PhoneNumberInputContract
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
    private val reducer: PhoneNumberInputReducer
) : ViewModel() {

    val configState = getConfigUseCase()
    private val _uiState = MutableStateFlow(PhoneNumberInputContract.State())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<PhoneNumberInputContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun dispatch(event: PhoneNumberInputContract.Event) {
        val (newState, effects) = reducer.reduce(_uiState.value, event)
        _uiState.value = newState
        effects.forEach { effect ->
            viewModelScope.launch { _effect.send(effect) }
        }
    }


}