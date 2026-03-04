package com.cresoty.catpossignpad.view.composable.setting

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpossignpad.ConfigKey
import com.cresoty.catpossignpad.model.interfaces.PadAction
import com.cresoty.catpossignpad.view.composable.common.ToggleAnimationButton
import com.cresoty.catpossignpad.view.controller.LocalController
import com.cresoty.catpossignpad.presentation.theme.CatposSignpadTheme
import com.cresoty.catpossignpad.presentation.theme.common01
import com.cresoty.catpossignpad.presentation.theme.common02
import com.cresoty.catpossignpad.presentation.theme.dpx
import com.cresoty.catpossignpad.presentation.theme.spx
import com.cresoty.catpossignpad.presentation.theme.white

@Composable
fun SettingIdVerify(
    modifier: Modifier
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()

    var isIdVerify by remember{ mutableStateOf(config.isIdVerify) }

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
            onClickClose = { controller.dispatch(PadAction.CloseDialog) },
            onClickSave = {
                val map = mutableMapOf<Preferences.Key<*>, Any>()
                map[ConfigKey.IS_ID_VERIFY] = isIdVerify
//                controller.dispatch(PadAction.OnClickSaveSetting(SettingType.ID_VERIFY, map))
            }
        )

    }
}

@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
fun SettingIdentificationPreview() {
    CatposSignpadTheme {
        SettingIdVerify(modifier = Modifier.fillMaxSize()
            .background(color = white))
    }
}
