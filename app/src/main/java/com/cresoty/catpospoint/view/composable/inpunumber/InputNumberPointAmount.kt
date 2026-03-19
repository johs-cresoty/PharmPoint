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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.model.enums.PointDeltaProcess
import com.cresoty.catpospoint.model.enums.PointUseSource
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
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun InputNumberPointAmount(
    modifier: Modifier,
    storeName : String,
    paymentAmount : String,
//    pointBalance : String
) {
    val controller = LocalController.current
    val main by controller.mainState.collectAsStateWithLifecycle()
    val backStep = (main.pointDeltaStep as? PointDeltaProcess.POINT_USE_AMOUNT_INPUT)
        ?.let { PointDeltaProcess.POINT_USE_PHONE_NUM(it.source) }
        ?: PointDeltaProcess.POINT_USE_PHONE_NUM(PointUseSource.MANUAL)

    val initialFontSize = 45f.spx
    var storeNameFontSize by remember(storeName) { mutableStateOf(initialFontSize) }
    var isFontSizeStabilized by remember(storeName) { mutableStateOf(false) }

    Column(
        modifier = modifier.background(white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackStepButton { controller.dispatch(PadAction.OnClickPointNext(backStep)) }

        Spacer(modifier = Modifier.size(27.dpx))

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

    }
}

@Preview(name = "사용 포인트 입력", device = "spec:width=800px,height=1319px,dpi=213")
@Composable
private fun InputNumberPointAmountPreview() {
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
            InputNumberPointAmount(
                modifier = Modifier.fillMaxSize().background(white),
                storeName = "OO약국",
                paymentAmount = "10,000"
            )
        }
    }
}
