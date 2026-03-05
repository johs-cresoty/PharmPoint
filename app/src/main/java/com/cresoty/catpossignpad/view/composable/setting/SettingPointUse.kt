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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpossignpad.ConfigKey
import com.cresoty.catpossignpad.model.interfaces.PadAction
import com.cresoty.catpossignpad.presentation.theme.CatposSignpadTheme
import com.cresoty.catpossignpad.presentation.theme.common02
import com.cresoty.catpossignpad.presentation.theme.dpx
import com.cresoty.catpossignpad.presentation.theme.spx
import com.cresoty.catpossignpad.presentation.theme.white
import com.cresoty.catpossignpad.view.composable.common.FilterTextField
import com.cresoty.catpossignpad.view.composable.common.FilterTextType
import com.cresoty.catpossignpad.view.composable.common.ToggleAnimationButton
import com.cresoty.catpossignpad.view.composable.list.SettingType
import com.cresoty.catpossignpad.view.controller.LocalController

@Composable
fun SettingPointUse(
    modifier: Modifier
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()

    var isMinPointEnabled by remember { mutableStateOf(config.isMinPointEnabled) }
    var minPoint by remember { mutableStateOf(config.minPoint.toString()) }

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

        //TODO : 포인트 사용 단위는 미정으로 차후 작업할지말지 결정
//        Column(
//            verticalArrangement = Arrangement.spacedBy(15f.dpx)
//        ) {
//            Text(
//                text = "포인트 사용 단위",
//                fontSize = 20f.spx,
//                fontWeight = FontWeight.Normal,
//                color = common02
//            )
//
//            FilterTextField(
//                modifier = Modifier.size(width = 275f.dpx, height = 55f.dpx),
//                textAlign = TextAlign.End,
//                initText = test2,
//                placeholder = "",
//                keyboardType = KeyboardType.Number,
//                filterType = FilterTextType.FILTER_TYPE_NUMBER,
//                hPadding = 20f.dpx,
//                vPadding = 14f.dpx,
//                fontColor = common01
//            )
//      }

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
            onClickClose = { controller.dispatch(PadAction.CloseDialog) },
            onClickSave = {
                val map = mutableMapOf<Preferences.Key<*>, Any>()
                map[ConfigKey.IS_MIN_POINT_ENABLED] = isMinPointEnabled
                map[ConfigKey.MINIMUM_POINT] = minPoint.replace(",", "").toIntOrNull() ?: 0
                controller.dispatch(PadAction.OnClickSaveSetting(SettingType.POINT_USE, map))
            }
        )


    }
}

@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
fun SettingPointUsePreview() {
    CatposSignpadTheme {
        SettingPointUse(
            Modifier
                .fillMaxSize()
                .background(color = white)
        )
    }
}
