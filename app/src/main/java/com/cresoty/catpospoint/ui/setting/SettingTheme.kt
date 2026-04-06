package com.cresoty.catpospoint.ui.setting

import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import com.cresoty.catpospoint.ui.component.ClickSoundButton
import com.cresoty.catpospoint.ui.component.FilterTextField
import com.cresoty.catpospoint.data.repository.ConfigKey
import com.cresoty.catpospoint.model.enums.MainThemes
import com.cresoty.catpospoint.model.state.ConfigState
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.main03
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.transparent
import com.cresoty.catpospoint.presentation.theme.white

@Composable
fun SettingTheme(
    modifier: Modifier,
    config: ConfigState,
    customThemeImageUri: Uri?,
    editingSubTitle: String?,
    editingThemeIndex: Int?,
    isSavedToastVisible: Boolean,
    onPanelState: (hasChanges: Boolean, doSave: () -> Unit) -> Unit = { _, _ -> },
    onSave: (Map<Preferences.Key<*>, Any>) -> Unit,
    onShowPreview: (theme: Int?, subTitle: String?, customImageUri: Uri?) -> Unit,
    onImageCropped: (Uri) -> Unit,
    onClose: () -> Unit,
) {
    val context = LocalContext.current

    var subTitle by remember { mutableStateOf(editingSubTitle ?: config.subTitle) }
    var selectedIndex by remember { mutableStateOf(editingThemeIndex ?: config.themeIndex) }

    val shape = RoundedCornerShape(100f.dpx)

    SideEffect {
        onPanelState(subTitle != config.subTitle || selectedIndex != config.themeIndex) {
            val map = mutableMapOf<Preferences.Key<*>, Any>()
            map[ConfigKey.SUB_TITLE] = subTitle
            map[ConfigKey.MAIN_THEME] = selectedIndex
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
                text = "서브 타이틀(최대 40자)",
                fontSize = 20f.spx,
                fontWeight = FontWeight.Normal,
                color = common02
            )

            FilterTextField(
                modifier = Modifier.size(width = 492f.dpx, height = 55f.dpx),
                placeholder = "전문 약사가 정성껏 상담해 드립니다.",
                initText = subTitle,
                textAlign = TextAlign.Start,
                maxLength = 40,
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
                        if (Settings.canDrawOverlays(context)) {
                            onShowPreview(selectedIndex, subTitle, customThemeImageUri)
                        } else {
                            context.startActivity(
                                Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                )
                            )
                        }
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
                customImageUri = customThemeImageUri,
                onClickTheme = { selection -> selectedIndex = selection },
                onCustomImageCropped = { uri ->
                    onImageCropped(uri)
                    if (uri == Uri.EMPTY) {
                        val themeAIndex = MainThemes.Theme_A.ordinal
                        selectedIndex = themeAIndex
                        val map = mutableMapOf<Preferences.Key<*>, Any>()
                        map[ConfigKey.SUB_TITLE] = subTitle
                        map[ConfigKey.MAIN_THEME] = themeAIndex
                        onSave(map)
                    }
                }
            )
        }

        SettingButtons(
            modifier = Modifier.weight(1f),
            isLogin = false,
            isSavedToastVisible = isSavedToastVisible,
            onClickClose = onClose,
            onClickSave = {
                val map = mutableMapOf<Preferences.Key<*>, Any>()
                map[ConfigKey.SUB_TITLE] = subTitle
                map[ConfigKey.MAIN_THEME] = selectedIndex
                onSave(map)
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 600, heightDp = 800)
@Composable
private fun SettingThemePreview() {
    SettingTheme(
        modifier = Modifier,
        config = ConfigState(subTitle = "전문 약사가 정성껏 상담해 드립니다."),
        customThemeImageUri = null,
        editingSubTitle = null,
        editingThemeIndex = null,
        isSavedToastVisible = false,
        onSave = {},
        onShowPreview = { _, _, _ -> },
        onImageCropped = {},
        onClose = {},
    )
}
