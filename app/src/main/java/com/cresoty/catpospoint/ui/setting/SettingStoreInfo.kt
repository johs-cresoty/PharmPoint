package com.cresoty.catpospoint.ui.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.core.Preferences
import com.cresoty.catpospoint.data.repository.ConfigKey
import com.cresoty.catpospoint.model.state.ConfigState
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.NotoSansKr
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main01
import com.cresoty.catpospoint.presentation.theme.notice
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white
import com.cresoty.catpospoint.remote.crypt.CresotyCrypt
import com.cresoty.catpospoint.ui.component.ClickSoundButton
import com.cresoty.catpospoint.ui.component.ConfirmButton
import com.cresoty.catpospoint.ui.component.FilterTextField
import com.cresoty.catpospoint.ui.component.FilterTextType

// 라벨 + 입력 필드 공용 컴포넌트
@Composable
fun SettingLabeledTextField(
    label: String? = null,
    placeholder: String,
    initText: String = "",
    filterType: FilterTextType = FilterTextType.NONE,
    maxLength: Int = Int.MAX_VALUE,
    onTextChange: (String) -> Unit = {},
    onClickDone: (String) -> Unit = {},
) {
    Column {
        if (label != null) {
            Text(
                text = label,
                color = common02,
                fontSize = 20f.spx
            )
            Spacer(modifier = Modifier.size(15f.dpx))
        }
        FilterTextField(
            modifier = Modifier.size(width = 351f.dpx, height = 55f.dpx),
            placeholder = placeholder,
            initText = initText,
            filterType = filterType,
            maxLength = maxLength,
            onTextChange = onTextChange,
            onClickDone = onClickDone,
        )
    }
}

@Composable
fun SettingStoreInfo(
    modifier: Modifier,
    config: ConfigState,
    isSavedToastVisible: Boolean,
    isPharmacyInvalid: Boolean = false,
    isValidatingPharmacy: Boolean = false,
    pharmacyInvalidMessage: String = "",
    onPanelState: (hasChanges: Boolean, doSave: () -> Unit) -> Unit = { _, _ -> },
    onBizNoChange: () -> Unit = {},
    onSave: (Map<Preferences.Key<*>, Any>) -> Unit,
    onClose: () -> Unit,
) {
    val isFirstSetup = config.bizNo.isEmpty()

    var bizNo by remember { mutableStateOf(config.bizNo) }
    var storeName by remember { mutableStateOf(config.storeName) }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }

    val passwordsValid =
        !isFirstSetup || (password.isNotEmpty() && password == passwordConfirm)
    val canSave = bizNo.length == 10 && passwordsValid

    SideEffect {
        onPanelState(canSave && (bizNo != config.bizNo || storeName != config.storeName)) {
            if (canSave) {
                val map = buildSaveMap(bizNo, storeName, isFirstSetup, password)
                onSave(map)
            }
        }
    }

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.size(40.dpx))

        SettingLabeledTextField(
            label = "약국명(최대 20자)",
            placeholder = "약국명을 입력해주세요.",
            initText = storeName,
            filterType = FilterTextType.NONE,
            maxLength = 20,
            onTextChange = { storeName = it },
            onClickDone = { storeName = it },
        )
        Spacer(modifier = Modifier.size(40.dpx))
        SettingLabeledTextField(
            label = "사업자번호",
            placeholder = "사업자번호 10자리를 입력해주세요.",
            initText = bizNo,
            filterType = FilterTextType.NUMBER,
            maxLength = 10,
            onTextChange = { bizNo = it; onBizNoChange() },
            onClickDone = { if (bizNo.length < 10) bizNo = it },
        )

        if (isPharmacyInvalid && pharmacyInvalidMessage.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(vertical = 10.dpx),
                text = pharmacyInvalidMessage,
                fontFamily = NotoSansKr,
                fontSize = 18.spx,
                color = notice
            )
        }

        Spacer(modifier = Modifier.size(40.dpx))
        if (isFirstSetup) {
            SettingLabeledTextField(
                label = "관리자 비밀번호(5 자리)",
                placeholder = "비밀번호를 입력해주세요.",
                maxLength = 5,
                filterType = FilterTextType.PASSWORD,
                onTextChange = { password = it },
                onClickDone = { password = it },
            )
            Spacer(modifier = Modifier.size(10.dpx))
            SettingLabeledTextField(
                placeholder = "비밀번호를 다시 입력해주세요.",
                filterType = FilterTextType.PASSWORD,
                maxLength = 5,
                onTextChange = { passwordConfirm = it },
                onClickDone = { passwordConfirm = it },
            )
            if (password.length == 5 && passwordConfirm.length == 5 && !passwordsValid) {
                Text(
                    modifier = Modifier.padding(vertical = 10.dpx),
                    text = "비밀번호가 일치하지 않습니다.",
                    fontFamily = NotoSansKr,
                    fontSize = 18.spx,
                    color = notice
                )
            } else {
                Text(
                    modifier = Modifier.padding(top = 10.dpx),
                    fontFamily = NotoSansKr,
                    fontSize = 18.spx,
                    color = common02,
                    lineHeight = 23.spx,
                    text = "환경 설정에 진입할 때 사용하는 관리자 비밀번호입니다.\n한 번 설정하면 변경할 수 없습니다."
                )
            }
        }

        // 저장 버튼 영역
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = isSavedToastVisible,
                enter = fadeIn(tween(50)),
                exit = fadeOut(tween(50))
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "저장되었습니다.",
                    color = main01,
                    fontSize = 25f.spx,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12f.dpx))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10f.dpx),
                verticalAlignment = Alignment.Bottom
            ) {
                if (!isFirstSetup) {
                    ClickSoundButton(
                        modifier = Modifier.size(width = 160f.dpx, height = 112f.dpx),
                        onClick = onClose,
                        border = BorderStroke(1f.dpx, common01),
                        backgroundColor = white,
                        shape = RoundedCornerShape(20f.dpx)
                    ) {
                        Text(
                            text = "닫기",
                            color = common01,
                            fontWeight = FontWeight.Medium,
                            fontSize = 35f.spx
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(width = 160f.dpx, height = 112f.dpx))
                }
                ConfirmButton(
                    modifier = Modifier.size(width = 350f.dpx, height = 112f.dpx),
                    text = "저장",
                    enabled = canSave,
                    isLoading = isValidatingPharmacy,
                    onClick = {
                        if (canSave) {
                            onSave(buildSaveMap(bizNo, storeName, isFirstSetup, password))
                        }
                    }
                )
            }
        }
    }
}

private fun buildSaveMap(
    bizNo: String,
    storeName: String,
    isFirstSetup: Boolean,
    password: String,
): Map<Preferences.Key<*>, Any> {
    val map = mutableMapOf<Preferences.Key<*>, Any>()
    map[ConfigKey.BIZ_NO] = bizNo
    map[ConfigKey.STORE_NAME] = storeName
    if (isFirstSetup && password.isNotEmpty()) {
        map[ConfigKey.PASSWORD] = CresotyCrypt.getAESEncode(password)
    }
    return map
}


@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
fun SettingStoreInfoPreview() {
    CatposPointTheme {
        SettingStoreInfo(
            modifier = Modifier
                .fillMaxSize()
                .background(color = white),
            config = ConfigState(),
            isSavedToastVisible = false,
            onSave = {},
            onClose = {},
        )
    }
}
