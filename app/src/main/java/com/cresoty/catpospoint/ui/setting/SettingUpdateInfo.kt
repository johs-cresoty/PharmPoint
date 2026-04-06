package com.cresoty.catpospoint.ui.setting

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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.BuildConfig
import com.cresoty.catpospoint.domain.model.UpdateInfo
import com.cresoty.catpospoint.presentation.setting.SettingContract
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.NotoSansKr
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main01
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white
import com.cresoty.catpospoint.ui.component.CloseButton
import com.cresoty.catpospoint.ui.component.ConfirmButton

@Composable
fun SettingUpdateInfo(
    modifier: Modifier,
    updateCheckState: SettingContract.UpdateCheckState,
    onCheckUpdate: () -> Unit,
    onAcceptUpdate: (installUrl: String) -> Unit,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        onCheckUpdate()
    }

    Column(modifier = modifier) {
        Text(
            text = "앱 버전: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            color = common02,
            fontSize = 20f.spx,
            fontFamily = NotoSansKr,
            modifier = Modifier.padding(top = 40.dpx, bottom = 20.dpx)
        )

        when (val state = updateCheckState) {
            is SettingContract.UpdateCheckState.Idle -> Unit

            is SettingContract.UpdateCheckState.Loading ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(25.dpx),
                        color = main01,
                        strokeWidth = 2.dpx
                    )

                    Spacer(modifier = Modifier.width(8.dpx))

                    Text(
                        text = "최신 버전 확인 중입니다...",
                        fontSize = 20.spx,
                        fontFamily = NotoSansKr,
                        color = common02
                    )
                }

            is SettingContract.UpdateCheckState.UpToDate ->
                Text(
                    text = "최신 버전입니다.",
                    color = common02,
                    fontFamily = NotoSansKr,
                    fontSize = 20.spx,
                )

            is SettingContract.UpdateCheckState.Error ->
                Text(
                    text = "업데이트 확인에 실패했습니다.",
                    color = common02,
                    fontFamily = NotoSansKr,
                    fontSize = 20.spx,
                )

            is SettingContract.UpdateCheckState.NeedUpdate -> {
                Text(
                    text = state.updateInfo.messageTitle,
                    fontFamily = NotoSansKr,
                    color = common02,
                    fontSize = 25.spx,
                )
                Text(
                    text = state.updateInfo.message,
                    fontFamily = NotoSansKr,
                    color = common02,
                    fontSize = 20.spx,
                )
                Spacer(Modifier.size(20.dpx))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    ConfirmButton(
                        text = "업데이트",
                        fontSize = 25.spx,
                        onClick = { onAcceptUpdate(state.updateInfo.installUrl) },
                        modifier = Modifier
                            .width(200.dpx)
                            .height(65.dpx),
                    )
                }

            }
        }
        Spacer(Modifier.weight(1f))
        CloseButton(
            fontSize = 35.spx,
            onClick = onDismiss,
            modifier = Modifier
                .height(112.dpx)
        )
    }
}

@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
private fun SettingUpdateInfoIdlePreview() {
    CatposPointTheme {
        SettingUpdateInfo(
            modifier = Modifier
                .fillMaxSize()
                .background(white),
            updateCheckState = SettingContract.UpdateCheckState.Idle,
            onCheckUpdate = {},
            onAcceptUpdate = {},
            onDismiss = {}
        )
    }
}

@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
private fun SettingUpdateInfoNeedUpdatePreview() {
    CatposPointTheme {
        SettingUpdateInfo(
            modifier = Modifier
                .fillMaxSize()
                .background(white),
            updateCheckState = SettingContract.UpdateCheckState.NeedUpdate(
                UpdateInfo(installUrl = "", messageTitle = "", message = "")
            ),
            onCheckUpdate = {},
            onAcceptUpdate = {},
            onDismiss = {}
        )
    }
}

@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
private fun SettingUpdateInfoUpToDatePreview() {
    CatposPointTheme {
        SettingUpdateInfo(
            modifier = Modifier
                .fillMaxSize()
                .background(white),
            updateCheckState = SettingContract.UpdateCheckState.UpToDate,
            onCheckUpdate = {},
            onAcceptUpdate = {},
            onDismiss = {}
        )
    }
}
