package com.cresoty.catpospoint.model.interfaces

import android.net.Uri
import com.cresoty.catpospoint.model.state.ConfigState
import com.cresoty.catpospoint.model.state.CustomerState
import com.cresoty.catpospoint.model.state.PointState
import com.cresoty.catpospoint.model.state.MainState
import com.cresoty.catpospoint.model.state.PreviewState
import com.cresoty.catpospoint.model.state.SettingState
import kotlinx.coroutines.flow.StateFlow

interface ViewController {
    val mainState: StateFlow<MainState>
    val configState: StateFlow<ConfigState>
    val previewState: StateFlow<PreviewState>
    val settingState: StateFlow<SettingState>
    val pointState: StateFlow<PointState>
    val customerState: StateFlow<CustomerState>
    val customThemeImageUriState: StateFlow<Uri?>

    fun dispatch(action: PadAction)
}