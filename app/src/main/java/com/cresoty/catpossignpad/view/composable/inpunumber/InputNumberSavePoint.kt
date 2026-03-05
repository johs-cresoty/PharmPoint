package com.cresoty.catpossignpad.view.composable.inpunumber

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpossignpad.R
import com.cresoty.catpossignpad.model.enums.PointDeltaProcess
import com.cresoty.catpossignpad.model.interfaces.PadAction
import com.cresoty.catpossignpad.model.interfaces.ViewController
import com.cresoty.catpossignpad.model.state.ConfigState
import com.cresoty.catpossignpad.model.state.CustomerState
import com.cresoty.catpossignpad.model.state.MainState
import com.cresoty.catpossignpad.model.state.PointState
import com.cresoty.catpossignpad.model.state.PreviewState
import com.cresoty.catpossignpad.model.state.SettingState
import com.cresoty.catpossignpad.toDecimalString
import com.cresoty.catpossignpad.presentation.component.BackStepButton
import com.cresoty.catpossignpad.presentation.theme.CatposSignpadTheme
import com.cresoty.catpossignpad.view.controller.LocalController
import com.cresoty.catpossignpad.presentation.theme.common02
import com.cresoty.catpossignpad.presentation.theme.main02
import com.cresoty.catpossignpad.presentation.theme.main04
import com.cresoty.catpossignpad.presentation.theme.common01
import com.cresoty.catpossignpad.presentation.theme.dpx
import com.cresoty.catpossignpad.presentation.theme.spx
import com.cresoty.catpossignpad.presentation.theme.transparent
import com.cresoty.catpossignpad.presentation.theme.white
import kotlinx.coroutines.flow.MutableStateFlow

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

    Column(
        modifier = modifier
            .background(color = white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackStepButton { controller.dispatch(PadAction.OnClickPointNext(PointDeltaProcess.NONE)) }

        Text(
            text = storeName,
            fontSize = 45f.spx,
            lineHeight = 45f.spx,
            color = common01,
            maxLines = 1
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
        override fun dispatch(action: PadAction) {}
    }
    CatposSignpadTheme {
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
        override fun dispatch(action: PadAction) {}
    }
    CatposSignpadTheme {
        CompositionLocalProvider(LocalController provides mockController) {
            InputNumberSavePoint(
                modifier = Modifier.fillMaxSize().background(white),
                storeName = "OO약국",
                paymentAmount = "10,000"
            )
        }
    }
}
