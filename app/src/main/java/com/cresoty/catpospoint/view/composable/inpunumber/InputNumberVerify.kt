package com.cresoty.catpospoint.view.composable.inpunumber

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white
import com.cresoty.catpospoint.view.controller.LocalController
import com.cresoty.catpospoint.model.event.AppEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun InputNumberVerify(
    modifier : Modifier,
    storeName : String,
    paymentAmount : String
) {
    val controller = LocalController.current
    val customer by controller.customerState.collectAsStateWithLifecycle()
    val isSuccess = customer.verifyResult

    val initialFontSize = 45f.spx
    var storeNameFontSize by remember(storeName) { mutableStateOf(initialFontSize) }
    var isFontSizeStabilized by remember(storeName) { mutableStateOf(false) }

    Column(
        modifier = modifier.background(white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackStepButton { controller.dispatch(PadAction.OnClickPointNext(PointDeltaProcess.NONE)) }

        Spacer(modifier = Modifier.size(38f.dpx))

        Text(
            modifier = Modifier.alpha(if (isFontSizeStabilized) 1f else 0f),
            text = storeName,
            fontSize = storeNameFontSize,
            lineHeight = storeNameFontSize,
            color = common01,
            maxLines = 1,
            softWrap = false,
            onTextLayout = {
                if (it.didOverflowWidth) storeNameFontSize *= 0.9f
                else isFontSizeStabilized = true
            }
        )

        Text(
            text = "${paymentAmount}원 결제",
            fontSize = 53f.spx,
            lineHeight = 53f.spx,
            fontWeight = FontWeight.Medium,
            color = common02
        )

        Spacer(modifier = Modifier.size(24f.dpx))

        Text(
            text = "알림톡을 확인하신 후 인증 번호를 입력해 주세요.",
            fontSize = 30f.spx,
            lineHeight = 30f.spx,
            color = common01
        )
    }
}

@Preview(name = "인증번호 입력", device = "spec:width=800px,height=1319px,dpi=213")
@Composable
private fun InputNumberVerifyPreview() {
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
            InputNumberVerify(
                modifier = Modifier.fillMaxSize().background(white),
                storeName = "건강과 행복이 열리는 중앙 메디칼 약국",
                paymentAmount = "10,000"
            )
        }
    }
}
