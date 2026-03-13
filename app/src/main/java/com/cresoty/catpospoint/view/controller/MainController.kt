package com.cresoty.catpospoint.view.controller

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpospoint.model.enums.PointDeltaProcess
import com.cresoty.catpospoint.model.interfaces.ViewController
import com.cresoty.catpospoint.model.interfaces.PadAction
import com.cresoty.catpospoint.model.state.ConfigState
import com.cresoty.catpospoint.model.state.CustomerState
import com.cresoty.catpospoint.model.state.MainState
import com.cresoty.catpospoint.model.state.PointState
import com.cresoty.catpospoint.model.state.PreviewState
import com.cresoty.catpospoint.model.state.SettingState
import com.cresoty.catpospoint.view.composable.MainIdleScreen
import com.cresoty.catpospoint.view.composable.PointDeltaProcDone
import com.cresoty.catpospoint.view.composable.RequestPointDelta
import com.cresoty.catpospoint.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.flow.StateFlow

val LocalController = staticCompositionLocalOf<ViewController> {
    error("PosController not provided")
}

@Composable
fun MainController(viewModel: MainViewModel, context: Context) {

    val controller = remember(viewModel) {
        object : ViewController {
            override val mainState: StateFlow<MainState> = viewModel.mainState
            override val configState: StateFlow<ConfigState> = viewModel.configState
            override val previewState: StateFlow<PreviewState> = viewModel.previewState
            override val settingState: StateFlow<SettingState> = viewModel.settingState
            override val pointState: StateFlow<PointState> = viewModel.pointState
            override val customerState: StateFlow<CustomerState> = viewModel.customerState
            override val customThemeImageUriState: StateFlow<Uri?> = viewModel.customThemeImageUriState

            override fun dispatch(action: PadAction) = viewModel.dispatch(action)

        }
    }

    CompositionLocalProvider(LocalController provides controller) {
        val preview by controller.previewState.collectAsStateWithLifecycle()
        val main by controller.mainState.collectAsStateWithLifecycle()
        val step = main.pointDeltaStep
        when(step) {
            PointDeltaProcess.NONE -> MainIdleScreen()

            PointDeltaProcess.POINT_SAVE_PHONE_NUM,
            PointDeltaProcess.POINT_USE_PHONE_NUM,
            PointDeltaProcess.POINT_USE_VERIFY_NUM,
            PointDeltaProcess.POINT_USE_AMOUNT_INPUT,
            PointDeltaProcess.REQUEST_NUM,
            PointDeltaProcess.REQUEST_CST -> RequestPointDelta(step)

            PointDeltaProcess.POINT_SAVE_PROC_DONE,
            PointDeltaProcess.POINT_USE_PROC_DONE,
            PointDeltaProcess.POINT_USE_PROC_SHORTAGE_FAIL-> PointDeltaProcDone(step)
        }


        DialogController()
    }


}
