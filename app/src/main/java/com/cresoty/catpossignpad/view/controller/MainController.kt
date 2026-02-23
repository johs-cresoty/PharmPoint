package com.cresoty.catpossignpad.view.controller

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpossignpad.model.enums.PointDeltaProcess
import com.cresoty.catpossignpad.model.interfaces.ViewController
import com.cresoty.catpossignpad.model.interfaces.PadAction
import com.cresoty.catpossignpad.model.state.ConfigState
import com.cresoty.catpossignpad.model.state.CustomerState
import com.cresoty.catpossignpad.model.state.MainState
import com.cresoty.catpossignpad.model.state.PointState
import com.cresoty.catpossignpad.model.state.PreviewState
import com.cresoty.catpossignpad.model.state.SettingState
import com.cresoty.catpossignpad.view.composable.MainIdleScreen
import com.cresoty.catpossignpad.view.composable.PointDeltaProcDone
import com.cresoty.catpossignpad.view.composable.RequestPointDelta
import com.cresoty.catpossignpad.viewmodel.MainViewModel
import kotlinx.coroutines.flow.StateFlow

val LocalController = staticCompositionLocalOf<ViewController> {
    error("PosController not provided")
}

@Composable
fun MainController(viewModel: MainViewModel, context: Context) {

    val controller = remember(viewModel) {
        object : ViewController {
            override val mainState : StateFlow<MainState> = viewModel.mainState
            override val configState : StateFlow<ConfigState> = viewModel.configState
            override val previewState : StateFlow<PreviewState> = viewModel.previewState
            override val settingState : StateFlow<SettingState> = viewModel.settingState
            override val pointState : StateFlow<PointState> = viewModel.pointState
            override val customerState : StateFlow<CustomerState> = viewModel.customerState

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
            PointDeltaProcess.POINT_USE_AMOUNT_INPUT -> RequestPointDelta(step)

            PointDeltaProcess.POINT_SAVE_PROC_DONE,
            PointDeltaProcess.POINT_USE_PROC_DONE,
            PointDeltaProcess.POINT_USE_PROC_SHORTAGE_FAIL-> PointDeltaProcDone(step)
        }


        if(!preview.isHideDialog)
            DialogController()
    }


}
