package com.cresoty.catpospoint.view.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.model.event.AppEvent
import com.cresoty.catpospoint.model.interfaces.PadAction
import com.cresoty.catpospoint.model.interfaces.ViewController
import com.cresoty.catpospoint.model.state.ConfigState
import com.cresoty.catpospoint.model.state.CustomerState
import com.cresoty.catpospoint.model.state.MainState
import com.cresoty.catpospoint.model.state.PointState
import com.cresoty.catpospoint.model.state.PreviewState
import com.cresoty.catpospoint.model.state.SettingState
import com.cresoty.catpospoint.presentation.component.ConfirmButton
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.NotoSansKr
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.view.composable.common.BaseDialog
import com.cresoty.catpospoint.view.controller.LocalController
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun UpdateBlockedDialog() {
    val controller = LocalController.current
    BaseDialog(onDismiss = {}) {
        Box(
            modifier = Modifier
                .width(542.dpx)
                .background(Color.White, RoundedCornerShape(20f.dpx))
                .padding(18.dpx)
        ) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(18.dpx)) {

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "업데이트 후에 사용 가능합니다",
                    color = common02,
                    fontSize = 30.spx,
                    lineHeight = 42.spx,
                    fontFamily = NotoSansKr,
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "업데이트 내용\n- 인증 기능이 추가됐어요\n- UI가 개선되었어요",
                    color = common02,
                    fontSize = 25.spx,
                    lineHeight = 30.spx,
                    fontFamily = NotoSansKr,
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Center
                )


                ConfirmButton(
                    text = "업데이트하기",
                    fontSize = 28f.spx,
                    onClick = { controller.dispatch(PadAction.OnAcceptUpdate) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84f.dpx)
                )
            }
        }
    }
}


@Preview(name = "업데이트 다이얼로그")
@Composable
private fun InputCustomerPhoneNumberPreview() {
    val mockController = object : ViewController {
        override val mainState = MutableStateFlow(MainState())
        override val configState = MutableStateFlow(ConfigState())
        override val previewState = MutableStateFlow(PreviewState())
        override val settingState = MutableStateFlow(SettingState())
        override val pointState = MutableStateFlow(PointState())
        override val customerState = MutableStateFlow(CustomerState())
        override val customThemeImageUriState = MutableStateFlow<android.net.Uri?>(null)
        override val appEvents: SharedFlow<AppEvent> = MutableSharedFlow()
        override fun dispatch(action: PadAction) {}
    }
    CatposPointTheme {
        CompositionLocalProvider(LocalController provides mockController) {
            UpdateBlockedDialog()
        }
    }
}