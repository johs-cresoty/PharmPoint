package com.cresoty.catpospoint.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.R
import com.cresoty.catpospoint.model.enums.PointDeltaProcess
import com.cresoty.catpospoint.model.interfaces.PadAction
import com.cresoty.catpospoint.model.interfaces.ViewController
import com.cresoty.catpospoint.model.state.ConfigState
import com.cresoty.catpospoint.model.state.CustomerState
import com.cresoty.catpospoint.model.state.MainState
import com.cresoty.catpospoint.model.state.PointState
import com.cresoty.catpospoint.model.state.PreviewState
import com.cresoty.catpospoint.model.state.SettingState
import com.cresoty.catpospoint.presentation.component.BackStepButton
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white
import com.cresoty.catpospoint.view.controller.LocalController
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun InputCustomerPhoneNumber(
    modifier: Modifier = Modifier,
    storeName: String,
) {
    val controller = LocalController.current

    Column(
        modifier = modifier
            .background(color = white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackStepButton { controller.dispatch(PadAction.OnClickPointNext(PointDeltaProcess.NONE)) }

        Image(
            painter = painterResource(R.drawable.logo_catposplus),
            contentDescription = null
        )

        Text(
            text = storeName,
            fontSize = 45.spx,
            fontWeight = FontWeight.Normal,
            color = common01,
            textAlign = TextAlign.Center,
            maxLines = 1
        )



        Spacer(modifier = Modifier.size(20f.dpx))

        Text(
            text = "휴대폰 번호 입력 후 확인 버튼을 눌러 주세요.",
            fontSize = 30f.spx,
            lineHeight = 30f.spx,
            color = common01
        )

        Spacer(modifier = Modifier.size(64.dpx))
    }
}


@Preview(name = "적립 - 예상포인트 없음", device = "spec:width=800px,height=1319px,dpi=213")
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
        override fun dispatch(action: PadAction) {}
    }
    CatposPointTheme {
        CompositionLocalProvider(LocalController provides mockController) {
            InputCustomerPhoneNumber(
                modifier = Modifier
                    .fillMaxSize()
                    .background(white),
                storeName = "OO약국",
            )
        }
    }
}