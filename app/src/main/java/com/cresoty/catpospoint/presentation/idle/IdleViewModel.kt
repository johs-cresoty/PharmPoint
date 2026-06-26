package com.cresoty.catpospoint.presentation.idle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cresoty.catpospoint.data.repository.ConfigKey
import com.cresoty.catpospoint.data.repository.ConfigRepository
import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.usecase.GetConfigUseCase
import com.cresoty.catpospoint.domain.usecase.GetPointAmountSettingUseCase
import com.cresoty.catpospoint.domain.usecase.GetPointSaveSettingUseCase
import com.cresoty.catpospoint.socket.SocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@HiltViewModel
class IdleViewModel @Inject constructor(
    private val socketManager: SocketManager,
    private val configRepo: ConfigRepository,
    private val getConfigUseCase: GetConfigUseCase,
    private val getPointSaveSettingUseCase: GetPointSaveSettingUseCase,
    private val getPointAmountSettingUseCase: GetPointAmountSettingUseCase,
    private val reducer: IdleReducer,
) : ViewModel() {

    val configState = getConfigUseCase()
    private val _uiState = MutableStateFlow(IdleContract.State())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<IdleContract.Effect>(Channel.Factory.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        initRequestPointSettings()

        // configState 변경 시 IdleContract.State.config 동기화
        configState.onEach { config ->
            _uiState.update { it.copy(config = config) }
        }.launchIn(viewModelScope)
    }

    fun dispatch(event: IdleContract.Event) {
        if (event is IdleContract.Event.OnTripleTap) {
            handleTripleTap()
            return
        }
        val (newState, effects) = reducer.reduce(_uiState.value, event)
        _uiState.value = newState
        effects.forEach { effect ->
            viewModelScope.launch { _effect.send(effect) }
        }
    }

    /**
     * 사업자번호 설정값이 비어있지 않을 경우 비밀번호 입력창 표출
     * 비어있을 경우 바로 설정 dialog 표출
     */
    private fun handleTripleTap() {
        val requiresPassword = configState.value.bizNo.isNotEmpty()
        viewModelScope.launch {
            _effect.send(IdleContract.Effect.ShowSettingDialog(requiresPassword))
        }
    }

    fun initRequestPointSettings() {
        val taxNo = configState.value.bizNo
        // 앱 최초 설치 후 약국 사업자번호 미설정 상태에서는 API 호출 skip.
        // 빈 taxNo 로 호출하면 서버가 "필수값 없음(1001)" 으로 응답해 Crashlytics 노이즈 발생.
        if (taxNo.isBlank()) return
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
}