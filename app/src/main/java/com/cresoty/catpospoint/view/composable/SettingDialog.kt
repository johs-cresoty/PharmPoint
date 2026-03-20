package com.cresoty.catpospoint.view.composable

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpospoint.model.interfaces.PadAction
import com.cresoty.catpospoint.view.composable.common.MessageDialog
import com.cresoty.catpospoint.model.interfaces.ViewController
import com.cresoty.catpospoint.model.state.ConfigState
import com.cresoty.catpospoint.model.state.CustomerState
import com.cresoty.catpospoint.model.state.MainState
import com.cresoty.catpospoint.model.state.PointState
import com.cresoty.catpospoint.model.state.PreviewState
import com.cresoty.catpospoint.model.state.SettingState
import com.cresoty.catpospoint.view.composable.common.BaseDialog
import com.cresoty.catpospoint.view.composable.list.SettingType
import com.cresoty.catpospoint.view.composable.list.SettingTypeList
import com.cresoty.catpospoint.view.composable.setting.SettingPointUse
import com.cresoty.catpospoint.view.composable.setting.SettingScreenTimeout
import com.cresoty.catpospoint.view.composable.setting.SettingStoreInfo
import com.cresoty.catpospoint.view.composable.setting.SettingTheme
import com.cresoty.catpospoint.view.controller.LocalController
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.sub01
import com.cresoty.catpospoint.presentation.theme.white
import com.cresoty.catpospoint.model.event.AppEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SettingDialog() {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()
    val setting by controller.settingState.collectAsStateWithLifecycle()
    val selectedIndex = setting.selectedIndex
    var selectedType = SettingType.entries[selectedIndex]

    var showBizNoAlert by remember { mutableStateOf(false) }
    var showChangesAlert by remember { mutableStateOf(false) }
    var pendingTabIndex by remember { mutableStateOf<Int?>(null) }

    var hasChanges by remember { mutableStateOf(false) }
    val doSaveHolder = remember { object { var fn: (() -> Unit)? = null } }
    val onPanelState: (Boolean, () -> Unit) -> Unit = { changes, save ->
        hasChanges = changes
        doSaveHolder.fn = save
    }

    if (showBizNoAlert) {
        MessageDialog(
            message = "사업자번호 저장 후 이용 가능합니다.",
            confirmText = "확인",
            dismissText = null,
            onConfirm = { showBizNoAlert = false },
            onDismiss = { showBizNoAlert = false }
        )
    }

    if (showChangesAlert) {
        MessageDialog(
            message = "변경사항이 있습니다.\n저장하시겠습니까?",
            confirmText = "예",
            dismissText = "아니오",
            onConfirm = {
                doSaveHolder.fn?.invoke()
                pendingTabIndex?.let { controller.dispatch(PadAction.OnClickSettingMenu(it)) }
                pendingTabIndex = null
                showChangesAlert = false
            },
            onDismiss = {
                pendingTabIndex?.let { controller.dispatch(PadAction.OnClickSettingMenu(it)) }
                pendingTabIndex = null
                showChangesAlert = false
            }
        )
    }

    BaseDialog(
        onCreate = {},
        onDismiss = {}
    ) {
        Row(
            modifier = Modifier.size(width = 742f.dpx, height = 953f.dpx)
                .background(color = sub01, shape = RoundedCornerShape(20f))
        ) {
            Column(
                modifier = Modifier.weight(0.25f)
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier.height(90f.dpx)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "환경설정",
                        fontSize = 30f.spx,
                        lineHeight = 30f.spx,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }

                SettingTypeList(
                    modifier = Modifier.weight(1f),
                    selectedIndex = selectedIndex
                ) { index, type ->
                    when {
                        config.bizNo.isEmpty() && index != 0 -> showBizNoAlert = true
                        hasChanges && index != selectedIndex -> {
                            pendingTabIndex = index
                            showChangesAlert = true
                        }
                        else -> {
                            controller.dispatch(PadAction.OnClickSettingMenu(index))
                            selectedType = type
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxHeight()
                    .background(color = white, shape = RoundedCornerShape(topEnd = 20f.dpx, bottomEnd = 20f.dpx))
                    .padding(18f.dpx)
            ) {
                SettingPanel(Modifier.weight(1f), selectedType, onPanelState)
            }

        }
    }
}

@Composable
fun SettingPanel(
    modifier: Modifier,
    type: SettingType,
    onPanelState: (hasChanges: Boolean, doSave: () -> Unit) -> Unit
) {
    when (type) {
        SettingType.STORE_INFO     -> SettingStoreInfo(modifier, onPanelState)
        SettingType.POINT_USE      -> SettingPointUse(modifier, onPanelState)
        SettingType.SCREEN_TIMEOUT -> SettingScreenTimeout(modifier, onPanelState)
        SettingType.THEME          -> SettingTheme(modifier, onPanelState)
    }
}

private val previewController = object : ViewController {
    override val mainState: StateFlow<MainState> = MutableStateFlow(MainState())
    override val configState: StateFlow<ConfigState> = MutableStateFlow(ConfigState())
    override val previewState: StateFlow<PreviewState> = MutableStateFlow(PreviewState())
    override val settingState: StateFlow<SettingState> = MutableStateFlow(SettingState())
    override val pointState: StateFlow<PointState> = MutableStateFlow(PointState())
    override val customerState: StateFlow<CustomerState> = MutableStateFlow(CustomerState())
    override val customThemeImageUriState: StateFlow<Uri?> = MutableStateFlow(null)
    override val appEvents: SharedFlow<AppEvent> = MutableSharedFlow()
    override fun dispatch(action: PadAction) {}
}

@Preview
@Composable
fun SettingDialogPreview() {
    CatposPointTheme {
        CompositionLocalProvider(LocalController provides previewController) {
            SettingDialog()
        }
    }
}
