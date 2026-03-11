package com.cresoty.catpospoint.view.composable.setting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpospoint.ConfigKey
import com.cresoty.catpospoint.model.interfaces.PadAction
import com.cresoty.catpospoint.presentation.component.ClickSoundButton
import com.cresoty.catpospoint.view.composable.list.SettingType
import com.cresoty.catpospoint.view.controller.LocalController
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.main03
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white

@Composable
fun SettingScreenTimeout(
    modifier: Modifier
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()
    val setting by controller.settingState.collectAsStateWithLifecycle()
    val list = listOf(
        "1", "2", "3", "4", "5"
    )
    val idx = list.indexOf(config.timeout.toString())
    var selectedIndex by remember{ mutableIntStateOf(idx) }

    val shape = RoundedCornerShape(5f.dpx)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(40f.dpx)
    ) {
        Spacer(modifier = Modifier.size(0f.dpx))    //간격 관리용 Spacer

        Column(
            verticalArrangement = Arrangement.spacedBy(15f.dpx)
        ) {
            Text(
                text = "화면 대기 시간",
                fontSize = 20f.spx,
                fontWeight = FontWeight.Normal,
                color = common02
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(5f.dpx)
            ){
                list.mapIndexed { index, text ->
                    val fontColor = if(index == selectedIndex) white else common01
                    val backgroundColor = if(index == selectedIndex) main03 else white
                    val borderColor = if(index == selectedIndex) main03 else common01
                    val fontWeight = if(index == selectedIndex) FontWeight.Bold else FontWeight.Normal

                    ClickSoundButton(
                        modifier = Modifier.size(width = 70f.dpx, height = 55f.dpx),
                        onClick = {
                            selectedIndex = index
                        },
                        shape = shape,
                        border = BorderStroke(width = 1f.dpx, color = borderColor),
                        backgroundColor = white
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                                .background(color = backgroundColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = text,
                                color = fontColor,
                                fontWeight = fontWeight
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.height(55f.dpx),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "초 후",
                        fontSize = 20f.spx,
                        fontWeight = FontWeight.Normal,
                        color = common01
                    )
                }
            }

            Text(
                text = "완료 화면 자동 꺼짐",
                fontSize = 20f.spx,
                fontWeight = FontWeight.Normal,
                color = common01
            )
        }

        SettingButtons(
            modifier = Modifier.weight(1f),
            isLogin = false,
            isSavedToastVisible = setting.isSavedToastVisible,
            onClickClose = { controller.dispatch(PadAction.CloseDialog) },
            onClickSave = {
                val map = mutableMapOf<Preferences.Key<*>, Any>()
                map[ConfigKey.SCREEN_TIMEOUT] = selectedIndex + 1
                controller.dispatch(PadAction.OnClickSaveSetting(SettingType.SCREEN_TIMEOUT, map))
            }
        )

    }
}

@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
fun SettingScreenTimeoutPreview() {
    CatposPointTheme {
        SettingScreenTimeout(
            Modifier.background(color = white)
            .fillMaxSize()
        )
    }
}
