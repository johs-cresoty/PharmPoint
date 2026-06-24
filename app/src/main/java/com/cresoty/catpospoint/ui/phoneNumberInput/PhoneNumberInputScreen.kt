package com.cresoty.catpospoint.ui.phoneNumberInput


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.R
import com.cresoty.catpospoint.model.enums.PointUseSource
import com.cresoty.catpospoint.presentation.phoneNumberInput.PhoneNumberInputContract
import com.cresoty.catpospoint.presentation.theme.AppTextStyle.SubTitle2
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.NotoSansKr
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main02
import com.cresoty.catpospoint.presentation.theme.main04
import com.cresoty.catpospoint.presentation.theme.notice
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white
import com.cresoty.catpospoint.toDecimalString
import com.cresoty.catpospoint.ui.component.AgreeCheckBox
import com.cresoty.catpospoint.ui.component.BackStepButton
import com.cresoty.catpospoint.ui.component.ConfirmButton
import com.cresoty.catpospoint.ui.component.NUMBER_PAD_KEYS
import com.cresoty.catpospoint.ui.component.NumberPadGrid
import com.cresoty.catpospoint.ui.component.PadKey
import com.cresoty.catpospoint.ui.component.PhoneMaskStrategy.HeadHalf
import com.cresoty.catpospoint.ui.component.PhoneNumberInputField
import com.cresoty.catpospoint.ui.dialog.MessageDialog

/**
 * 휴대폰 번호 입력 화면
 * 고객 조회 CAT, TERMINAL
 * 포인트 적립 CAT, TERMINAL
 */
@Composable
fun PhoneNumberInputScreen(
    modifier: Modifier = Modifier,
    state: PhoneNumberInputContract.State,
    sendEvent: (PhoneNumberInputContract.Event) -> Unit,
) {
    val mode = state.mode
    val isInvalidCustomer = mode is PhoneNumberInputContract.Mode.Lookup &&
            state.isCustomerExist == false
    val isConfirmEnabled = state.isCheckBox && state.phoneNumber.length == 11
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .background(color = white)
            .padding(start = 40.dpx, end = 40.dpx, top = 45.dpx),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackStepButton { sendEvent(PhoneNumberInputContract.Event.OnClickClose) }

        when {
            mode is PhoneNumberInputContract.Mode.Lookup && mode.source == PointUseSource.CAT ->
                TerminalInputInfo(storeName = state.storeName, payAmount = state.payAmount)

            mode is PhoneNumberInputContract.Mode.Lookup && mode.source == PointUseSource.TERMINAL ->
                TerminalInputInfo(storeName = state.storeName, payAmount = state.payAmount)

            mode is PhoneNumberInputContract.Mode.Lookup && mode.source == PointUseSource.MANUAL ->
                CheckPointInfo(storeName = state.storeName)

            mode is PhoneNumberInputContract.Mode.CatRequestCustomer || mode is PhoneNumberInputContract.Mode.CatRequestNum -> CATPOSInputInfo(
                storeName = state.storeName
            )

            else ->
                PhoneNumberInputInfo(
                    storeName = state.storeName,
                    payAmount = state.payAmount,
                    estimatedPoint = state.estimatedPoint
                )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dpx),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isInvalidCustomer) {
                Image(
                    modifier = Modifier.size(height = 24.dpx, width = 25.dpx),
                    painter = painterResource(R.drawable.icon_alert),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.size(12.dpx))

                Text(
                    text = "등록된 회원이 없습니다.",
                    fontSize = 25f.spx,
                    color = notice
                )
            }
        }

        PhoneNumberInputField(
            value = state.phoneNumber,
            maskStrategy = HeadHalf,
            isMasked = state.isMasked,
            onMaskToggle = { sendEvent(PhoneNumberInputContract.Event.OnMaskToggle) },
            isRegisteredCustomer = !isInvalidCustomer
        )
        Spacer(modifier = Modifier.weight(1f))
        NumberPadGrid(
            modifier = Modifier.size(width = 720.dpx, height = 440.dpx),
            keys = NUMBER_PAD_KEYS,
            onKeyPress = { key ->
                when (key) {
                    is PadKey.Number -> sendEvent(PhoneNumberInputContract.Event.OnNumberInput(key.digit))
                    PadKey.DeleteOne -> sendEvent(PhoneNumberInputContract.Event.OnDeleteOne)
                    PadKey.DeleteAll -> sendEvent(PhoneNumberInputContract.Event.OnDeleteAll)
                }
            }
        )
        Spacer(modifier = Modifier.size(24.dpx))
        AgreeCheckBox(
            isCheckBox = state.isCheckBox,
            onClick = { sendEvent(PhoneNumberInputContract.Event.OnClickCheckBox(!state.isCheckBox)) }
        )
        Spacer(modifier = Modifier.size(23.dpx))
        ConfirmButton(
            modifier = Modifier.height(112.dpx),
            text = "확인",
            onClick = {
                sendEvent(PhoneNumberInputContract.Event.OnClickConfirm(state.phoneNumber))
            },
            enabled = isConfirmEnabled,
            isLoading = state.isLoading,
        )
        Box(
            modifier = Modifier
                .height(80.dpx)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    sendEvent(PhoneNumberInputContract.Event.OnClickClose)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "다음에 하기",
                style = SubTitle2.copy(fontSize = 25.spx)
            )
        }

    }

    // 서버/네트워크 에러 안내 다이얼로그
    state.errorMessage?.let { message ->
        MessageDialog(
            title = "안내",
            message = message,
            confirmText = "확인",
            onConfirm = { sendEvent(PhoneNumberInputContract.Event.OnDismissErrorDialog) },
        )
    }
}


@Composable
fun CheckPointInfo(storeName: String) {
    val initialFontSize = 45f.spx
    var storeNameFontSize by remember(storeName) { mutableStateOf(initialFontSize) }
    Column(
        modifier = Modifier.background(white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.size(27.dpx))

        Text(
            text = storeName,
            fontSize = storeNameFontSize,
            lineHeight = storeNameFontSize,
            color = common01,
            maxLines = 1,
            softWrap = false,
            onTextLayout = {
                if (it.didOverflowWidth) storeNameFontSize *= 0.9f
            }
        )



        Spacer(modifier = Modifier.size(96.dpx))

        Text(
            text = "휴대폰 번호 입력하고 포인트 확인하세요.",
            fontSize = 30f.spx,
            lineHeight = 30f.spx,
            color = common01
        )
    }
}

@Composable
fun PhoneNumberInputInfo(storeName: String, payAmount: Int, estimatedPoint: Int) {
    val initialFontSize = 45f.spx
    var storeNameFontSize by remember(storeName) { mutableStateOf(initialFontSize) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = storeName,
            fontSize = storeNameFontSize,
            lineHeight = 60.75.spx,
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(400),
            color = common01,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            onTextLayout = {
                if (it.didOverflowWidth) storeNameFontSize *= 0.9f
            }
        )
        Text(
            text = "${payAmount.toDecimalString()}원 결제",
            fontSize = 53.spx,
            lineHeight = 71.55.spx,
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(500),
            color = common02,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        if (estimatedPoint > 0) {
            Spacer(modifier = Modifier.size(21.dpx))
            Row(
                modifier = Modifier
                    .background(
                        color = main04,
                        shape = RoundedCornerShape(100.dpx)
                    )
                    .padding(
                        top = 15.dpx,
                        bottom = 15.dpx,
                        start = 15.dpx,
                        end = 25.dpx
                    ),
                horizontalArrangement = Arrangement.spacedBy(20f.dpx),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .padding(1.dpx)
                        .width(35.dpx)
                        .height(35.dpx),
                    painter = painterResource(R.drawable.icon_won),
                    contentDescription = null
                )

                Text(
                    text = "${estimatedPoint.toDecimalString()}P 적립예상",
                    fontSize = 30.spx,
                    fontFamily = NotoSansKr,
                    fontWeight = FontWeight(500),
                    color = main02,
                    textAlign = TextAlign.Right

                )
            }
            Spacer(modifier = Modifier.size(20.dpx))
        } else {
            Spacer(modifier = Modifier.size(24.dpx))
        }


        Text(
            text = "휴대폰 번호 입력 후 확인 버튼을 눌러 주세요.",
            fontSize = 30.spx,
            lineHeight = 40.5.spx,
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(400),
            color = common01
        )
    }
}

@Composable
fun CATPOSInputInfo(storeName: String) {
    val initialFontSize = 45f.spx
    var storeNameFontSize by remember(storeName) { mutableStateOf(initialFontSize) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.size(21.5.dpx))
        Image(
            modifier = Modifier.size(height = 51.dpx, width = 250.dpx),
            painter = painterResource(R.drawable.logo_catposplus),
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(34.5.dpx))
        Text(
            text = storeName,
            fontSize = storeNameFontSize,
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(400),
            color = common01,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            onTextLayout = {
                if (it.didOverflowWidth) storeNameFontSize *= 0.9f
            }
        )

        Spacer(modifier = Modifier.size(23.dpx))

        Text(
            text = "휴대폰 번호 입력 후 확인 버튼을 눌러 주세요.",
            fontSize = 30.spx,
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(400),
            color = common01
        )
    }

}

@Composable
fun TerminalInputInfo(storeName: String, payAmount: Int) {
    val initialFontSize = 45f.spx
    var storeNameFontSize by remember(storeName) { mutableStateOf(initialFontSize) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.size(27.dpx))
        Text(
            text = storeName,
            fontSize = storeNameFontSize,
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(400),
            color = common01,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            onTextLayout = {
                if (it.didOverflowWidth) storeNameFontSize *= 0.9f
            }
        )
        Text(
            text = "${payAmount.toDecimalString()}원 결제",
            fontSize = 53.spx,
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(500),
            color = common02,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Spacer(modifier = Modifier.size(24.dpx))

        Text(
            text = "휴대폰 번호 입력 후 확인 버튼을 눌러 주세요.",
            fontSize = 30.spx,
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(400),
            color = common01
        )
    }

}


@Preview(name = "적립 - TERMINAL", device = "spec:width=800px,height=1340px,dpi=213")
@Composable
private fun PhoneNumberScreenSaveTerminalPreview() {
    CatposPointTheme {
        PhoneNumberInputScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(white),
            state = PhoneNumberInputContract.State(
                mode = PhoneNumberInputContract.Mode.Save(PointUseSource.TERMINAL),
                phoneNumber = "01012345678",
                storeName = "크레소티",
                payAmount = 10000,
                estimatedPoint = 1500
            ),
            sendEvent = {}
        )
    }
}

@Preview(name = "조회 - CAT", device = "spec:width=800px,height=1340px,dpi=213")
@Composable
private fun PhoneNumberScreenLookupCatPreview() {
    CatposPointTheme {
        PhoneNumberInputScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(white),
            state = PhoneNumberInputContract.State(
                mode = PhoneNumberInputContract.Mode.CatRequestCustomer,
                phoneNumber = "01012345678",
                storeName = "크레소티"
            ),
            sendEvent = {}
        )
    }
}

@Preview(name = "조회 - TERMINAL", device = "spec:width=800px,height=1340px,dpi=213")
@Composable
private fun PhoneNumberScreenLookupTerminalPreview() {
    CatposPointTheme {
        PhoneNumberInputScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(white),
            state = PhoneNumberInputContract.State(
                mode = PhoneNumberInputContract.Mode.Lookup(PointUseSource.TERMINAL),
                isCustomerExist = false,
                phoneNumber = "01012345678",
                storeName = "크레소티",
                payAmount = 10000
            ),
            sendEvent = {}
        )
    }
}

@Preview(
    name = "포인트 적립 - 예상포인트 있음",
    device = "spec:width=800px,height=1340px,dpi=213",
    showBackground = true
)
@Composable
private fun PhoneNumberInputInfoPreview() {
    CatposPointTheme {
        PhoneNumberInputInfo(
            storeName = "일이삼사오육칠팔구십일이삼사오육칠팔구십",
            payAmount = 10000,
            estimatedPoint = 5000,
        )
    }
}

@Preview(
    name = "고객 조회 - 캣포스",
    device = "spec:width=800px,height=1340px,dpi=213",
    showBackground = true
)
@Composable
private fun CATPOSInputInfoPreview() {
    CatposPointTheme {
        CATPOSInputInfo(storeName = "다나아약국")
    }
}

@Preview(
    name = "고객 조회 - 단말기",
    device = "spec:width=800px,height=1340px,dpi=213",
    showBackground = true
)
@Composable
private fun TerminalInputInfoPreview() {
    CatposPointTheme {
        TerminalInputInfo(storeName = "다나아약국", payAmount = 5000)
    }
}

@Preview(
    name = "포인트 조회 - MANUAL",
    device = "spec:width=800px,height=1340px,dpi=213",
    showBackground = true
)
@Composable
private fun CheckPointInfoPreview() {
    CatposPointTheme {
        CheckPointInfo(storeName = "다나아약국")
    }
}
