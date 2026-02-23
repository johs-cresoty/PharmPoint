package com.cresoty.catpossignpad.model.interfaces

import com.cresoty.catpossignpad.model.state.ConfigState
import com.cresoty.catpossignpad.model.state.CustomerState
import com.cresoty.catpossignpad.model.state.PointState
import com.cresoty.catpossignpad.model.state.MainState
import com.cresoty.catpossignpad.model.state.PreviewState
import com.cresoty.catpossignpad.model.state.SettingState
import kotlinx.coroutines.flow.StateFlow

interface ViewController {
    val mainState : StateFlow<MainState>
    val configState : StateFlow<ConfigState>
    val previewState : StateFlow<PreviewState>
    val settingState : StateFlow<SettingState>
    val pointState : StateFlow<PointState>
    val customerState : StateFlow<CustomerState>

    fun dispatch(action : PadAction)
}