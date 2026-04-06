package com.cresoty.catpospoint.ui.dialog

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.datastore.preferences.core.Preferences
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpospoint.model.state.ConfigState
import com.cresoty.catpospoint.presentation.setting.SettingContract
import com.cresoty.catpospoint.presentation.setting.SettingViewModel
import com.cresoty.catpospoint.ui.component.BaseDialog
import com.cresoty.catpospoint.ui.component.MessageDialog
import com.cresoty.catpospoint.ui.setting.SettingType
import com.cresoty.catpospoint.ui.setting.SettingTypeList
import com.cresoty.catpospoint.ui.setting.SettingPointUse
import com.cresoty.catpospoint.ui.setting.SettingScreenTimeout
import com.cresoty.catpospoint.ui.setting.SettingStoreInfo
import com.cresoty.catpospoint.ui.setting.SettingTheme
import com.cresoty.catpospoint.ui.setting.SettingUpdateInfo
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.sub01
import com.cresoty.catpospoint.presentation.theme.white

@Composable
fun SettingDialog(vm: SettingViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val config by vm.configState.collectAsStateWithLifecycle()
    val selectedIndex = state.selectedMenuIndex
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
                pendingTabIndex?.let { vm.dispatch(SettingContract.Event.SelectMenu(it)) }
                pendingTabIndex = null
                showChangesAlert = false
            },
            onDismiss = {
                pendingTabIndex?.let { vm.dispatch(SettingContract.Event.SelectMenu(it)) }
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
                            vm.dispatch(SettingContract.Event.SelectMenu(index))
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
                SettingPanel(
                    modifier = Modifier.weight(1f),
                    type = selectedType,
                    onPanelState = onPanelState,
                    config = config,
                    state = state,
                    onSave = { type, map -> vm.dispatch(SettingContract.Event.SaveSetting(type, map)) },
                    onShowPreview = { theme, subTitle, customImageUri ->
                        vm.dispatch(SettingContract.Event.StartPreview(theme, subTitle, customImageUri))
                    },
                    onImageCropped = { uri -> vm.dispatch(SettingContract.Event.CropCustomThemeImage(uri)) },
                    onClose = { vm.dispatch(SettingContract.Event.CloseDialog) },
                    onCheckUpdate = { vm.dispatch(SettingContract.Event.CheckUpdate) },
                    onAcceptSettingUpdate = { installUrl -> vm.dispatch(SettingContract.Event.OnClickSettingUpdate(installUrl)) },
                )
            }

        }
    }
}

@Composable
fun SettingPanel(
    modifier: Modifier,
    type: SettingType,
    onPanelState: (hasChanges: Boolean, doSave: () -> Unit) -> Unit,
    config: ConfigState,
    state: SettingContract.State,
    onSave: (SettingType, Map<Preferences.Key<*>, Any>) -> Unit,
    onShowPreview: (theme: Int?, subTitle: String?, customImageUri: Uri?) -> Unit,
    onImageCropped: (Uri) -> Unit,
    onClose: () -> Unit,
    onCheckUpdate: () -> Unit = {},
    onAcceptSettingUpdate: (installUrl: String) -> Unit = {},
) {
    when (type) {
        SettingType.STORE_INFO -> SettingStoreInfo(
            modifier = modifier,
            config = config,
            isSavedToastVisible = state.isSavedToastVisible,
            onPanelState = onPanelState,
            onSave = { map -> onSave(SettingType.STORE_INFO, map) },
            onClose = onClose,
        )
        SettingType.POINT_USE -> SettingPointUse(
            modifier = modifier,
            config = config,
            isSavedToastVisible = state.isSavedToastVisible,
            onPanelState = onPanelState,
            onSave = { map -> onSave(SettingType.POINT_USE, map) },
            onClose = onClose,
        )
        SettingType.SCREEN_TIMEOUT -> SettingScreenTimeout(
            modifier = modifier,
            config = config,
            isSavedToastVisible = state.isSavedToastVisible,
            onPanelState = onPanelState,
            onSave = { map -> onSave(SettingType.SCREEN_TIMEOUT, map) },
            onClose = onClose,
        )
        SettingType.THEME -> SettingTheme(
            modifier = modifier,
            config = config,
            customThemeImageUri = state.customThemeImageUri,
            editingSubTitle = state.editingSubTitle,
            editingThemeIndex = state.editingThemeIndex,
            isSavedToastVisible = state.isSavedToastVisible,
            onPanelState = onPanelState,
            onSave = { map -> onSave(SettingType.THEME, map) },
            onShowPreview = onShowPreview,
            onImageCropped = onImageCropped,
            onClose = onClose,
        )
        SettingType.UPDATE -> SettingUpdateInfo(
            modifier = modifier,
            updateCheckState = state.updateCheckState,
            onCheckUpdate = { onCheckUpdate() },
            onAcceptUpdate = { installUrl -> onAcceptSettingUpdate(installUrl) },
            onDismiss = onClose
        )
    }
}
