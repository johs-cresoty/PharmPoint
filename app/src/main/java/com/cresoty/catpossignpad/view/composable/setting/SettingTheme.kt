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
import com.cresoty.catpossignpad.view.composable.common.FilterTextField
import com.cresoty.catpossignpad.ConfigKey
import com.cresoty.catpossignpad.model.interfaces.PadAction
import com.cresoty.catpossignpad.presentation.component.ClickSoundButton
import com.cresoty.catpossignpad.view.composable.list.SettingMainThemeList
import com.cresoty.catpossignpad.view.composable.list.SettingType
import com.cresoty.catpossignpad.view.controller.LocalController
import com.cresoty.catpossignpad.presentation.theme.CatposSignpadTheme
import com.cresoty.catpossignpad.presentation.theme.common02
import com.cresoty.catpossignpad.presentation.theme.main03
import com.cresoty.catpossignpad.presentation.theme.dpx
import com.cresoty.catpossignpad.presentation.theme.spx
import com.cresoty.catpossignpad.presentation.theme.transparent
import com.cresoty.catpossignpad.presentation.theme.white

@Composable
fun SettingTheme(
    modifier: Modifier
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()

    var subTitle by remember{ mutableStateOf(config.subTitle) }

    var selectedIndex by remember{ mutableStateOf(config.themeIndex) }

    val shape = RoundedCornerShape(100f.dpx)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(40f.dpx)
    ) {
        Spacer(modifier = Modifier.size(0f.dpx))    //간격 관리용 Spacer

        Column(
            verticalArrangement = Arrangement.spacedBy(15f.dpx)
        ) {
            Text(
                //20자는 바이트 기준인지 글자수 기준인지?
                text = "서브 타이틀(최대 20자)",
                fontSize = 20f.spx,
                fontWeight = FontWeight.Normal,
                color = common02
            )

            FilterTextField(
                modifier = Modifier.size(width = 492f.dpx, height = 55f.dpx),
                placeholder = "전문 약사가 정성껏 상담해 드립니다.",
                initText = subTitle,
                textAlign = TextAlign.Start,
                onTextChange = { subTitle = it },
                onClickDone = { subTitle = it }
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(15f.dpx)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10f.dpx),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "테마 선택",
                    fontSize = 20f.spx,
                    fontWeight = FontWeight.Normal,
                    color = common02
                )

                ClickSoundButton(
                    modifier = Modifier.size(width = 82f.dpx, height = 26f.dpx),
                    shape = shape,
                    onClick = {
                        controller.dispatch(PadAction.OnClickShowPreview(selectedIndex, subTitle))
                    },
                    backgroundColor = transparent
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(color = main03)
                            .padding(vertical = 3f.dpx),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "미리보기",
                            color = white,
                            fontSize = 15f.spx,
                            lineHeight = 15f.spx,
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
    CatposSignpadTheme {
        SettingTheme(
            Modifier.fillMaxSize()
                .background(color = white)
        )
    }
}
