package com.cresoty.catpospoint.view.composable.setting

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpospoint.view.composable.common.FilterTextField
import com.cresoty.catpospoint.view.composable.common.FilterTextType
import com.cresoty.catpospoint.ConfigKey
import com.cresoty.catpospoint.model.interfaces.PadAction
import com.cresoty.catpospoint.view.composable.list.SettingType
import com.cresoty.catpospoint.view.controller.LocalController
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white

@Composable
fun SettingStoreInfo(
    modifier: Modifier
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()

    var bizNo by remember { mutableStateOf(config.bizNo) }
    var storeName by remember { mutableStateOf(config.storeName) }

    val list = listOf(
        Triple(ConfigKey.STORE_NAME, KeyboardType.Text, storeName),
        Triple(ConfigKey.BIZ_NO, KeyboardType.Number, bizNo)
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(40f.dpx)
    ) {
        Spacer(modifier = Modifier.size(0f.dpx))    //간격 관리용 Spacer

        list.mapIndexed { index, item ->
            val filter = if(item.first == ConfigKey.STORE_NAME) FilterTextType.NONE else FilterTextType.NUMBER
            val placeholder = if(item.first == ConfigKey.STORE_NAME) "약국명을 입력해주세요."
            else "사업자번호 10자리를 입력해주세요."

            Column{
                Text(
                    text = if(index == 0) "약국명" else "사업자번호",
                    color = common02,
                    fontSize = 20f.spx
                )

                Spacer(modifier = Modifier.size(15f.dpx))

                FilterTextField(
                    modifier = Modifier.size(width = 351f.dpx, height = 55f.dpx),
                    placeholder = placeholder,
                    initText = item.third,
                    filterType = filter,
                    onTextChange = {
                        if(item.first == ConfigKey.BIZ_NO) {
                            bizNo = it
                            if(bizNo.length > 10)
                                bizNo = bizNo.dropLast(1)
                        }
                        else
                            storeName = it
                    },
                    onClickDone = {
                        if(item.first == ConfigKey.BIZ_NO) {
                            if(bizNo.length < 10) bizNo = it
                        }
                        else
                            storeName = it
                    }
                )
            }
        }

        SettingButtons(
            modifier = Modifier.weight(1f),
            isLogin = false,
            enabled = bizNo.length == 10 && storeName.isNotEmpty(),
            onClickClose = { controller.dispatch(PadAction.CloseDialog) },
            onClickSave = {
                if(bizNo.length == 10 && storeName.isNotEmpty()) {
                    val map = mutableMapOf<Preferences.Key<*>, Any>()
                    map[ConfigKey.BIZ_NO] = bizNo
                    map[ConfigKey.STORE_NAME] = storeName

                    controller.dispatch(PadAction.OnClickSaveSetting(SettingType.STORE_INFO, map))
                }
            }
        )
    }
}

@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
fun SettingStoreInfoPreview() {
    CatposPointTheme {
        SettingStoreInfo(
            Modifier.fillMaxSize()
            .background(color = white)
        )
    }
}
