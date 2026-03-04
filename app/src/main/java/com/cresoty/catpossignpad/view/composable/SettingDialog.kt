package com.cresoty.catpossignpad.view.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpossignpad.model.interfaces.PadAction
import com.cresoty.catpossignpad.view.composable.common.BaseDialog
import com.cresoty.catpossignpad.view.composable.list.SettingTypeList
import com.cresoty.catpossignpad.view.composable.list.SettingType
import com.cresoty.catpossignpad.view.composable.setting.SettingPointUse
import com.cresoty.catpossignpad.view.composable.setting.SettingScreenTimeout
import com.cresoty.catpossignpad.view.composable.setting.SettingStoreInfo
import com.cresoty.catpossignpad.view.composable.setting.SettingTheme
import com.cresoty.catpossignpad.view.controller.LocalController
import com.cresoty.catpossignpad.presentation.theme.CatposSignpadTheme
import com.cresoty.catpossignpad.presentation.theme.dpx
import com.cresoty.catpossignpad.presentation.theme.spx
import com.cresoty.catpossignpad.presentation.theme.sub01
import com.cresoty.catpossignpad.presentation.theme.white

@Composable
fun SettingDialog() {
    val controller = LocalController.current
    val setting by controller.settingState.collectAsStateWithLifecycle()
    val selectedIndex = setting.selectedIndex
    var selectedType = SettingType.entries[selectedIndex]

    BaseDialog(
        onCreate = {},
        onDismiss = {}
    ) {
        Row(
            modifier = Modifier.size(width = 742f.dpx, height = 953f.dpx)
                .background(color = sub01, shape = RoundedCornerShape(20f))
        ) {
            Column(
                modifier = Modifier.weight(0.25f)
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier.height(90f.dpx)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "환경설정",
                        fontSize = 30f.spx,
                        lineHeight = 30f.spx,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }

                SettingTypeList(
                    modifier = Modifier.weight(1f),
                    selectedIndex = selectedIndex
                ) { index, type ->
                    controller.dispatch(PadAction.OnClickSettingMenu(index))
                    selectedType = type
                }
            }

            Column(
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxHeight()
                    .background(color = white, shape = RoundedCornerShape(topEnd = 20f.dpx, bottomEnd = 20f.dpx))
                    .padding(18f.dpx)
            ) {
                SettingPanel(Modifier.weight(1f), selectedType)
            }

        }
    }
}

@Composable
fun SettingPanel(
    modifier: Modifier,
    type : SettingType
) {
    val block = remember {
        mapOf<SettingType, @Composable () -> Unit> (
            SettingType.STORE_INFO      to { SettingStoreInfo(modifier) },
//            SettingType.ID_VERIFY       to { SettingIdVerify(modifier) },
            SettingType.POINT_USE       to { SettingPointUse(modifier) },
            SettingType.SCREEN_TIMEOUT  to { SettingScreenTimeout(modifier) },
            SettingType.THEME           to { SettingTheme(modifier) }
            )
    }

    block.getValue(type).invoke()
}

@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
fun SettingDialogPreview() {
    CatposSignpadTheme {
        SettingDialog()
    }
}
