package com.cresoty.catpospoint.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.core.Preferences
import com.cresoty.catpospoint.model.state.ConfigState
import com.cresoty.catpospoint.ui.component.ToggleAnimationButton
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white

@Composable
fun SettingIdVerify(
    modifier: Modifier,
    config: ConfigState,
    isSavedToastVisible: Boolean,
    onSave: (Map<Preferences.Key<*>, Any>) -> Unit = {},
    onClose: () -> Unit,
) {
    var isIdVerify by remember { mutableStateOf(config.isIdVerify) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(40f.dpx)
    ) {
        Spacer(modifier = Modifier.size(0f.dpx))    //간격 관리용 Spacer

        Column(
            verticalArrangement = Arrangement.spacedBy(15f.dpx)
        ) {
            Text(
                text = "본인인증 진행 여부",
                fontWeight = FontWeight.Normal,
                color = common02,
                fontSize = 20f.spx
            )

            ToggleAnimationButton(
                modifier = Modifier.size(width = 275f.dpx,height = 55f.dpx),
                isSelected = isIdVerify
            ) {
                isIdVerify = it
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(15f.dpx)
        ) {
            Text(
                text = "포인트 사용 시\n고객본인인증절차 진행 여부를 선택합니다.",
                fontWeight = FontWeight.Normal,
                color = common01,
                fontSize = 20f.spx
            )

            Text(
                text = "사용으로 설정한 경우\n알림톡 또는 SMS 이용 요금이 발생합니다.",
                fontWeight = FontWeight.Normal,
                color = common01,
                fontSize = 20f.spx
            )
        }

        SettingButtons(
            modifier = Modifier.weight(1f),
            isLogin = false,
            isSavedToastVisible = isSavedToastVisible,
            onClickClose = onClose,
            onClickSave = {
//                val map = mutableMapOf<Preferences.Key<*>, Any>()
//                map[ConfigKey.IS_ID_VERIFY] = isIdVerify
//                onSave(map)
            }
        )

    }
}

@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
fun SettingIdentificationPreview() {
    CatposPointTheme {
        SettingIdVerify(
            modifier = Modifier.fillMaxSize().background(color = white),
            config = ConfigState(),
            isSavedToastVisible = false,
            onClose = {},
        )
    }
}
