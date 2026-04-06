package com.cresoty.catpospoint.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogWindowProvider
import androidx.datastore.preferences.core.Preferences
import com.cresoty.catpospoint.data.repository.ConfigKey
import com.cresoty.catpospoint.model.state.ConfigState
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main03
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white
import kotlin.math.roundToInt

@Composable
fun SettingBrightness(
    modifier: Modifier,
    config: ConfigState,
    isSavedToastVisible: Boolean,
    onPanelState: (hasChanges: Boolean, doSave: () -> Unit) -> Unit = { _, _ -> },
    onSave: (Map<Preferences.Key<*>, Any>) -> Unit,
    onClose: () -> Unit,
) {
    var brightness by remember { mutableFloatStateOf(config.brightness) }
    val percent = (brightness * 100).roundToInt()

    // SettingBrightness는 BaseDialog(Dialog) 안에 있으므로 Dialog 자체의 window를 사용해야 함
    // Activity window를 업데이트해도 Dialog window가 앞에 덮고 있어서 적용되지 않음
    val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window

    SideEffect {
        // 슬라이더 값이 바뀔 때마다 Dialog window 밝기 실시간 반영
        dialogWindow?.attributes = dialogWindow?.attributes?.apply {
            screenBrightness = brightness
        }
        onPanelState(brightness != config.brightness) {
            val map = mutableMapOf<Preferences.Key<*>, Any>()
            map[ConfigKey.BRIGHTNESS] = brightness
            onSave(map)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(40f.dpx)
    ) {
        Spacer(modifier = Modifier.size(0f.dpx))

        Column(
            verticalArrangement = Arrangement.spacedBy(15f.dpx)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "화면 밝기",
                    fontSize = 20f.spx,
                    fontWeight = FontWeight.Normal,
                    color = common02
                )
                Text(
                    text = "$percent%",
                    fontSize = 20f.spx,
                    fontWeight = FontWeight.Bold,
                    color = main03
                )
            }

            // valueRange = 0f..1f 로 설정해야 brightness=0.1f(10%)일 때
            // 트랙이 10% 채워진 것처럼 보임 (0.1f..1.0f 사용 시 맨 왼쪽 = 0% 처럼 보이는 문제 해결)
            Slider(
                value = brightness,
                onValueChange = { brightness = it.coerceAtLeast(0.1f) },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = main03,
                    activeTrackColor = main03,
                    inactiveTrackColor = common01,
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "최소 10% ~ 최대 100% 조절 가능합니다.",
                fontSize = 16f.spx,
                fontWeight = FontWeight.Normal,
                color = common01
            )
        }

        SettingButtons(
            modifier = Modifier.weight(1f),
            isLogin = false,
            isSavedToastVisible = isSavedToastVisible,
            onClickClose = onClose,
            onClickSave = {
                val map = mutableMapOf<Preferences.Key<*>, Any>()
                map[ConfigKey.BRIGHTNESS] = brightness
                onSave(map)
            }
        )
    }
}

@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
fun SettingBrightnessPreview() {
    CatposPointTheme {
        SettingBrightness(
            modifier = Modifier.background(color = white).fillMaxSize(),
            config = ConfigState(),
            isSavedToastVisible = false,
            onSave = {},
            onClose = {},
        )
    }
}
