package com.cresoty.catpospoint.ui.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.R
import com.cresoty.catpospoint.model.interfaces.PadAction
import com.cresoty.catpospoint.model.interfaces.ViewController
import com.cresoty.catpospoint.model.state.ConfigState
import com.cresoty.catpospoint.model.state.CustomerState
import com.cresoty.catpospoint.model.state.MainState
import com.cresoty.catpospoint.model.state.PointState
import com.cresoty.catpospoint.model.state.PreviewState
import com.cresoty.catpospoint.model.state.SettingState
import com.cresoty.catpospoint.presentation.component.ConfirmButton
import com.cresoty.catpospoint.presentation.result.ResultContract
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.NotoSansKr
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main01
import com.cresoty.catpospoint.presentation.theme.main04
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white
import com.cresoty.catpospoint.toDecimalString
import com.cresoty.catpospoint.view.controller.LocalController
import kotlinx.coroutines.delay
import com.cresoty.catpospoint.model.event.AppEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun ResultScreen(
    state: ResultContract.State,
    sendEvent: (ResultContract.Event) -> Unit
) {
    var leftTime by remember { mutableIntStateOf(state.timeOut) }
    LaunchedEffect(Unit) {
        while (leftTime != 0) {
            delay(1000)

            leftTime -= 1
        }
        sendEvent(ResultContract.Event.GoToWaiting)
//        controller.dispatch(PadAction.OnClickPointNext(PointDeltaProcess.NONE))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white)
            .padding(horizontal = 40f.dpx),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.size(288.dpx))
        Image(
            painter = painterResource(R.drawable.icon_store),
            contentDescription = null
        )

        Spacer(modifier = Modifier.size(51.dpx))

        if (state.title.isNotEmpty()) {
            Text(
                text = state.title,
                fontSize = 53.spx,
                color = common02,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = state.subTitle,
            style = TextStyle(
                fontSize = 30.spx,
                lineHeight = 40.5.spx,
                fontFamily = NotoSansKr,
                fontWeight = FontWeight(400),
                color = common01,
                textAlign = TextAlign.Center,
            )
        )


        Spacer(modifier = Modifier.size(52.dpx))

        Row(
            modifier = Modifier
                .height(110.dpx)
                .background(color = main04, shape = RoundedCornerShape(100f.dpx))
                .padding(horizontal = 44f.dpx),
            horizontalArrangement = Arrangement.spacedBy(10f.dpx),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.pointTitle,
                fontSize = 30.spx,
                color = main01
            )

            Text(
                text = state.balancePoint.toDecimalString(),
                fontSize = 40.spx,
                color = main01,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "P",
                fontSize = 35.spx,
                color = main01,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.size(261.dpx))

        Text(
            text = "${leftTime}초 후 창 자동 닫힘",
            fontSize = 30f.spx,
            color = common02
        )

        Spacer(modifier = Modifier.weight(1f))

        ConfirmButton(
            modifier = Modifier.height(110.dpx),
            onClick = { sendEvent(ResultContract.Event.GoToWaiting) })
        Spacer(modifier = Modifier.size(80.dpx))
    }
}

@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
private fun ResultScreenPreview() {
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
            ResultScreen(
                state = ResultContract.State().copy(
                    subTitle = "다나아약국\n" +
                            "현재 보유하고 있는 포인트입니다.", pointTitle = "보유 포인트", balancePoint = 112865
                ),
                sendEvent = {}
            )
        }
    }
}