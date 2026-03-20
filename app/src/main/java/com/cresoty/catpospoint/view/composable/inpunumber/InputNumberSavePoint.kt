package com.cresoty.catpospoint.view.composable.inpunumber

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.cresoty.catpospoint.toDecimalString
import com.cresoty.catpospoint.presentation.component.BackStepButton
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.view.controller.LocalController
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.main02
import com.cresoty.catpospoint.presentation.theme.main04
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.transparent
import com.cresoty.catpospoint.presentation.theme.white
import com.cresoty.catpospoint.model.event.AppEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun InputNumberSavePoint(
    modifier : Modifier = Modifier,
    storeName : String,
    paymentAmount : String,
) {
    val controller = LocalController.current

    val point by controller.pointState.collectAsStateWithLifecycle()
    val pointDelta = point.pointDelta.toDecimalString()

    val isVisible = if(pointDelta == "0") 0f else 1f

    val initialFontSize = 45f.spx
    var storeNameFontSize by remember(storeName) { mutableStateOf(initialFontSize) }
    var isFontSizeStabilized by remember(storeName) { mutableStateOf(false) }

    Column(
        modifier = modifier
            .background(color = white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackStepButton { controller.dispatch(PadAction.OnClickPointNext(PointDeltaProcess.NONE)) }

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

        Spacer(modifier = Modifier.size(6.dpx))

        Row(
            modifier = Modifier.background(
                color = if(isVisible == 0f) transparent else main04,
                shape = RoundedCornerShape(100f.dpx)
            )
                .alpha(isVisible)
                .padding(
                    top = 15f.dpx,
                    bottom = 15f.dpx,
                    start = 25f.dpx,
                    end = 15f.dpx
                ),
            horizontalArrangement = Arrangement.spacedBy(20f.dpx)
        ) {
            Image(
                painter = painterResource(R.drawable.icon_won),
                contentDescription = null
            )

            Text(
                text = "${pointDelta}P 적립예상",
                fontSize = 30f.spx,
                lineHeight = 30f.spx,
                fontWeight = FontWeight.Medium,
                color = main02
            )
        }

        Spacer(modifier = Modifier.size(20f.dpx))

        Text(
            text = "휴대폰 번호 입력하고 포인트 받아가세요.",
            fontSize = 30f.spx,
            lineHeight = 30f.spx,
            color = common01
        )

        Spacer(modifier = Modifier.size(64.dpx))
    }
}

@Preview(name = "적립 - 예상포인트 없음", device = "spec:width=800px,height=1319px,dpi=213")
@Composable
private fun InputNumberSavePointEmptyPreview() {
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
            InputNumberSavePoint(
                modifier = Modifier.fillMaxSize().background(white),
                storeName = "OO약국",
                paymentAmount = "10,000"
            )
        }
    }
}

@Preview(name = "적립 - 예상포인트 있음", device = "spec:width=800px,height=1319px,dpi=213")
@Composable
private fun InputNumberSavePointWithPointPreview() {
    val mockController = object : ViewController {
        override val mainState = MutableStateFlow(MainState())
        override val configState = MutableStateFlow(ConfigState())
        override val previewState = MutableStateFlow(PreviewState())
        override val settingState = MutableStateFlow(SettingState())
        override val pointState = MutableStateFlow(PointState(pointDelta = "500"))
        override val customerState = MutableStateFlow(CustomerState())
        override val customThemeImageUriState = MutableStateFlow<android.net.Uri?>(null)
        override val appEvents: SharedFlow<AppEvent> = MutableSharedFlow()
        override fun dispatch(action: PadAction) {}
    }
    CatposPointTheme {
        CompositionLocalProvider(LocalController provides mockController) {
            InputNumberSavePoint(
                modifier = Modifier.fillMaxSize().background(white),
                storeName = "OO약국",
                paymentAmount = "10,000"
            )
        }
    }
}
