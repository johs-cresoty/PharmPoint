package com.cresoty.catpospoint.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.core.Preferences
import com.cresoty.catpospoint.data.repository.ConfigKey
import com.cresoty.catpospoint.model.state.ConfigState
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.white
import com.cresoty.catpospoint.ui.component.TimeoutOptionSelector

private val AUTO_CLOSE_VALUES = listOf(1, 2, 3, 4, 5)
private val INACTIVE_CLOSE_VALUES = listOf(10, 15, 20, 25, 30)

@Composable
fun SettingScreenTimeout(
    modifier: Modifier,
    config: ConfigState,
    isSavedToastVisible: Boolean,
    onPanelState: (hasChanges: Boolean, doSave: () -> Unit) -> Unit = { _, _ -> },
    onSave: (Map<Preferences.Key<*>, Any>) -> Unit,
    onClose: () -> Unit,
) {
    val autoCloseInitIdx = AUTO_CLOSE_VALUES.indexOf(config.autoCloseTimeout).coerceAtLeast(0)
    val inactiveCloseInitIdx = INACTIVE_CLOSE_VALUES.indexOf(config.inactiveCloseTimeout).coerceAtLeast(0)

    var autoCloseIdx by remember(config.autoCloseTimeout) {
        mutableIntStateOf(autoCloseInitIdx)
    }
    var inactiveCloseIdx by remember(config.inactiveCloseTimeout) {
        mutableIntStateOf(inactiveCloseInitIdx)
    }

    val selectedAutoClose = AUTO_CLOSE_VALUES[autoCloseIdx]
    val selectedInactiveClose = INACTIVE_CLOSE_VALUES[inactiveCloseIdx]

    val hasChanges = selectedAutoClose != config.autoCloseTimeout ||
        selectedInactiveClose != config.inactiveCloseTimeout

    SideEffect {
        onPanelState(hasChanges) {
            val map = mutableMapOf<Preferences.Key<*>, Any>()
            map[ConfigKey.AUTO_CLOSE_TIMEOUT] = selectedAutoClose
            map[ConfigKey.INACTIVE_CLOSE_TIMEOUT] = selectedInactiveClose
            onSave(map)
        }
    }


    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(40f.dpx)
    ) {
        Spacer(modifier = Modifier.size(0f.dpx))    //간격 관리용 Spacer

        TimeoutOptionSelector(
            title = "화면 대기 시간",
            subTitle = "완료 화면 자동 꺼짐",
            options = AUTO_CLOSE_VALUES,
            selectedIndex = autoCloseIdx,
            onSelectedChange = { autoCloseIdx = it },
        )

        TimeoutOptionSelector(
            title = "미동작 시 대기 시간",
            subTitle = "포인트 적립/사용 화면에서 미동작 시 자동 꺼짐",
            options = INACTIVE_CLOSE_VALUES,
            selectedIndex = inactiveCloseIdx,
            onSelectedChange = { inactiveCloseIdx = it },
        )

        SettingButtons(
            modifier = Modifier.weight(1f),
            isLogin = false,
            isSavedToastVisible = isSavedToastVisible,
            onClickClose = onClose,
            onClickSave = {
                val map = mutableMapOf<Preferences.Key<*>, Any>()
                map[ConfigKey.AUTO_CLOSE_TIMEOUT] = selectedAutoClose
                map[ConfigKey.INACTIVE_CLOSE_TIMEOUT] = selectedInactiveClose
                onSave(map)
            }
        )

    }
}

@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
fun SettingScreenTimeoutPreview() {
    CatposPointTheme {
        SettingScreenTimeout(
            modifier = Modifier
                .background(color = white)
                .fillMaxSize(),
            config = ConfigState(),
            isSavedToastVisible = false,
            onSave = {},
            onClose = {},
        )
    }
}
