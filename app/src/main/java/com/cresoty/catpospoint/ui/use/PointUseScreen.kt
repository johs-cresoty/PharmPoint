package com.cresoty.catpospoint.ui.use

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.model.enums.PointQuickInputType
import com.cresoty.catpospoint.presentation.theme.AppTextStyle.SubTitle2
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.NotoSansKr
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main01
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.sub01
import com.cresoty.catpospoint.presentation.theme.sub02
import com.cresoty.catpospoint.presentation.theme.white
import com.cresoty.catpospoint.presentation.use.UseContract
import com.cresoty.catpospoint.toDecimalString
import com.cresoty.catpospoint.ui.component.BackStepButton
import com.cresoty.catpospoint.ui.component.ClickSoundButton
import com.cresoty.catpospoint.ui.component.ConfirmButton
import com.cresoty.catpospoint.ui.component.NUMBER_PAD_KEYS
import com.cresoty.catpospoint.ui.component.NumberPadGrid
import com.cresoty.catpospoint.ui.component.PadKey

@Composable
fun PointUseScreen(
    state: UseContract.State,
    sendEvent: (UseContract.Event) -> Unit,
) {

    val interactionSource = remember { MutableInteractionSource() }
    val buttonList = PointQuickInputType.entries
    val amount = if (state.usePoint < 1) "얼마인가요?" else "${state.usePoint} P"
    val fontColor = if (state.usePoint < 1) sub02 else main01
    val fontWeight = if (state.usePoint < 1) FontWeight.Normal else FontWeight.Bold
    val isConfirmEnabled =
        if (state.isMinPointEnabled) state.usePoint >= state.minPoint else state.usePoint > 0

    Column(
        modifier = Modifier
            .background(color = white)
            .padding(start = 40.dpx, end = 40.dpx, top = 45.dpx),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackStepButton { }
        Spacer(modifier = Modifier.size(27.dpx))
        Text(
            text = state.storeName,
            fontSize = 45.spx,
            lineHeight = 60.75.spx,
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(400),
            color = common01,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            text = "${state.payAmount.toDecimalString()}원 결제",
            fontSize = 53.spx,
            lineHeight = 71.55.spx,
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(500),
            color = common02,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Spacer(modifier = Modifier.size(34.dpx))
        Text(
            text = "사용하실 포인트는",
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(400),
            fontSize = 50.spx,
            lineHeight = 67.5.spx,
            color = main01
        )

        Text(
            text = amount,
            fontFamily = NotoSansKr,
            fontWeight = fontWeight,
            fontSize = 50.spx,
            lineHeight = 67.5.spx,
            color = fontColor
        )

        Spacer(modifier = Modifier.size(31.dpx))

        Text(
            text = "보유 포인트 ${state.balancePoint}P",
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(400),
            fontSize = 30.spx,
            lineHeight = 40.5.spx,
            color = common01
        )

        Spacer(modifier = Modifier.size(23.dpx))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10f.dpx)
        ) {
            buttonList.mapIndexed { index, item ->
                val easyInput =
                    if (index != buttonList.lastIndex) "+ ${item.amount}" else item.amount
                ClickSoundButton(
                    modifier = Modifier.size(width = 150f.dpx, height = 60f.dpx),
                    backgroundColor = sub01,
                    onClick = {

//                        controller.dispatch(PadAction.OnClickAmountQuickButton(item))
                    },
                    shape = RoundedCornerShape(50f.dpx)
                ) {
                    Text(
                        text = easyInput,
                        fontSize = 25f.spx,
                        color = common01
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(14.dpx))
        NumberPadGrid(
            modifier = Modifier.size(width = 720.dpx, height = 440.dpx),
            keys = NUMBER_PAD_KEYS,
            onKeyPress = { key ->
                when (key) {
                    is PadKey.Number -> sendEvent(UseContract.Event.OnNumberInput(key.digit))
                    PadKey.DeleteOne -> sendEvent(UseContract.Event.OnDeleteOne)
                    PadKey.DeleteAll -> sendEvent(UseContract.Event.OnDeleteAll)
                }
            }
        )
        Spacer(modifier = Modifier.size(24.dpx))
        if (state.isMinPointEnabled) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dpx),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "포인트는 최소 ${state.minPoint.toDecimalString()}P부터 사용 가능합니다.",
                    fontSize = 30.spx,
                    fontWeight = FontWeight(400),
                    lineHeight = 30.spx,
                    color = main01
                )
            }
        }
        Spacer(modifier = Modifier.size(23.dpx))
        ConfirmButton(
            modifier = Modifier.height(112.dpx),
            text = "확인",
            onClick = {
                sendEvent(UseContract.Event.OnClickConfirm(state.usePoint))
            },
            enabled = isConfirmEnabled
        )
        Box(
            modifier = Modifier
                .height(80.dpx)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    sendEvent(UseContract.Event.OnClickClose)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "다음에 하기",
                style = SubTitle2.copy(fontSize = 25.spx)
            )
        }
    }
}

@Preview(
    name = "포인트 사용",
    device = "spec:width=800px,height=1340px,dpi=213",
    showBackground = true
)
@Composable
private fun PointUseScreenPreview() {
    val dummyState = UseContract.State(
        storeName = "다나아 약국",
        payAmount = 15000,
        usePoint = 0,
        balancePoint = 10000,
        minPoint = 1000,
        isMinPointEnabled = true
    )
    CatposPointTheme {
        PointUseScreen(state = dummyState, sendEvent = {})
    }
}
