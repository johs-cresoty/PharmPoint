package com.cresoty.catpospoint.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.core.Preferences
import com.cresoty.catpospoint.data.repository.ConfigKey
import com.cresoty.catpospoint.model.state.ConfigState
import com.cresoty.catpospoint.ui.component.FilterTextField
import com.cresoty.catpospoint.ui.component.FilterTextType
import com.cresoty.catpospoint.ui.component.ToggleAnimationButton
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white

@Composable
fun SettingPointUse(
    modifier: Modifier,
    config: ConfigState,
    isSavedToastVisible: Boolean,
    onPanelState: (hasChanges: Boolean, doSave: () -> Unit) -> Unit = { _, _ -> },
    onSave: (Map<Preferences.Key<*>, Any>) -> Unit,
    onClose: () -> Unit,
) {
    var isMinPointEnabled by remember { mutableStateOf(config.isMinPointEnabled) }
    var minPoint by remember { mutableStateOf(config.minPoint.toString()) }

    SideEffect {
        val currentMinPoint = minPoint.replace(",", "").toIntOrNull() ?: 0
        val canSave = !isMinPointEnabled || currentMinPoint > 0
        onPanelState(canSave && (isMinPointEnabled != config.isMinPointEnabled || currentMinPoint != config.minPoint)) {
            val map = mutableMapOf<Preferences.Key<*>, Any>()
            map[ConfigKey.IS_MIN_POINT_ENABLED] = isMinPointEnabled
            map[ConfigKey.MINIMUM_POINT] = minPoint.replace(",", "").toIntOrNull() ?: 0
            onSave(map)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(40f.dpx)
    ) {
        Spacer(modifier = Modifier.size(0f.dpx))    //간격 관리용 Spacer

        Column(
            verticalArrangement = Arrangement.spacedBy(15f.dpx)
        ) {
            Text(
                text = "최소 포인트 사용 여부",
                fontSize = 20f.spx,
                fontWeight = FontWeight.Normal,
                color = common02
            )

            ToggleAnimationButton(
                modifier = Modifier.size(width = 275f.dpx, height = 55f.dpx),
                isSelected = isMinPointEnabled
            ) {
                isMinPointEnabled = it
            }
        }

        if (isMinPointEnabled) {
            Column(
                verticalArrangement = Arrangement.spacedBy(15f.dpx)
            ) {
                Text(
                    text = "최소 사용 포인트",
                    fontSize = 20f.spx,
                    fontWeight = FontWeight.Normal,
                    color = common02
                )

                FilterTextField(
                    modifier = Modifier.size(width = 275f.dpx, height = 55f.dpx),
                    textAlign = TextAlign.End,
                    initText = minPoint,
                    placeholder = "0",
                    filterType = FilterTextType.PRICE,
                    onTextChange = { minPoint = it },
                    onClickDone = { minPoint = it }
                )
            }
        }


        SettingButtons(
            modifier = Modifier.weight(1f),
            isLogin = false,
            enabled = !isMinPointEnabled || (minPoint.isNotEmpty() && (minPoint.replace(",", "")
                .toIntOrNull() ?: -1) > 0),
            isSavedToastVisible = isSavedToastVisible,
            onClickClose = onClose,
            onClickSave = {
                val map = mutableMapOf<Preferences.Key<*>, Any>()
                map[ConfigKey.IS_MIN_POINT_ENABLED] = isMinPointEnabled
                map[ConfigKey.MINIMUM_POINT] = minPoint.replace(",", "").toIntOrNull() ?: 0
                onSave(map)
            }
        )


    }
}

@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
fun SettingPointUsePreview() {
    CatposPointTheme {
        SettingPointUse(
            modifier = Modifier
                .fillMaxSize()
                .background(color = white),
            config = ConfigState(),
            isSavedToastVisible = false,
            onSave = {},
            onClose = {},
        )
    }
}
