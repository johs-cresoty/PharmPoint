package com.cresoty.catpossignpad.view.composable.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.PharmpayPos.view.composable.FilterTextField
import com.cresoty.catpossignpad.ConfigKey
import com.cresoty.catpossignpad.model.interfaces.PadAction
import com.cresoty.catpossignpad.px2dp
import com.cresoty.catpossignpad.px2sp
import com.cresoty.catpossignpad.view.composable.common.ClickSoundButton
import com.cresoty.catpossignpad.view.composable.list.SettingMainThemeList
import com.cresoty.catpossignpad.view.composable.list.SettingType
import com.cresoty.catpossignpad.view.controller.LocalController
import com.cresoty.catpossignpad.view.theme.common02
import com.cresoty.catpossignpad.view.theme.main03
import com.cresoty.catpossignpad.view.theme.transparent
import com.cresoty.catpossignpad.view.theme.white

@Composable
fun SettingTheme(
    modifier: Modifier
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()

    var subTitle by remember{ mutableStateOf(config.subTitle) }

    var selectedIndex by remember{ mutableStateOf(config.themeIndex) }

    val shape = RoundedCornerShape(100f.px2dp())

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(40f.px2dp())
    ) {
        Spacer(modifier = Modifier.size(0f.px2dp()))    //간격 관리용 Spacer

        Column(
            verticalArrangement = Arrangement.spacedBy(15f.px2dp())
        ) {
            Text(
                //20자는 바이트 기준인지 글자수 기준인지?
                text = "서브 타이틀(최대 20자)",
                fontSize = 20f.px2sp(),
                fontWeight = FontWeight.Normal,
                color = common02
            )

            FilterTextField(
                modifier = Modifier.size(width = 492f.px2dp(), height = 55f.px2dp()),
                placeholder = "전문 약사가 정성껏 상담해 드립니다.",
                initText = subTitle,
                fontSize = 20f.px2sp(),
                fontColor = common02,
                textAlign = TextAlign.Start,
                onTextChange = { subTitle = it },
                onClickDone = { subTitle = it }
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(15f.px2dp())
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10f.px2dp()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "테마 선택",
                    fontSize = 20f.px2sp(),
                    fontWeight = FontWeight.Normal,
                    color = common02
                )

                ClickSoundButton(
                    modifier = Modifier.size(width = 82f.px2dp(), height = 26f.px2dp()),
                    shape = shape,
                    onClick = {
                        controller.dispatch(PadAction.OnClickShowPreview(selectedIndex, subTitle))
                    },
                    backgroundColor = transparent
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(color = main03)
                            .padding(vertical = 3f.px2dp()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "미리보기",
                            color = white,
                            fontSize = 15f.px2sp(),
                            lineHeight = 15f.px2sp(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            SettingMainThemeList(
                selectedIndex = selectedIndex,
            ) { selection ->
                selectedIndex = selection
            }
        }

        SettingButtons(
            modifier = Modifier.weight(1f),
            isLogin = false,
            onClickClose = { controller.dispatch(PadAction.CloseDialog) },
            onClickSave = {
                val map = mutableMapOf<Preferences.Key<*>, Any>()
                map[ConfigKey.SUB_TITLE] = subTitle
                map[ConfigKey.MAIN_THEME] = selectedIndex
                controller.dispatch(PadAction.OnClickSaveSetting(SettingType.THEME, map))
            }
        )
    }
}

@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
fun SettingThemePreview() {
    SettingTheme(
        Modifier.fillMaxSize()
            .background(color = white)
    )
}