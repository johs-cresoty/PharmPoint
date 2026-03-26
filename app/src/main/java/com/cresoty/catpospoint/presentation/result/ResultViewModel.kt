package com.cresoty.catpospoint.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ResultViewModel @Inject constructor(
    private val reducer: ResultReducer,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultContract.State())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<ResultContract.Effect>(Channel.Factory.BUFFERED)
    val effect = _effect.receiveAsFlow()


    fun dispatch(event: ResultContract.Event) {
        val (newState, effects) = reducer.reduce(_uiState.value, event)
        _uiState.value = newState
        effects.forEach { effect ->
            viewModelScope.launch { _effect.send(effect) }
        }
    }
}