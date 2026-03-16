package com.cresoty.catpospoint.ui.component


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.R
import com.cresoty.catpospoint.model.enums.PointDeltaProcess
import com.cresoty.catpospoint.model.enums.PointQuickInputType
import com.cresoty.catpospoint.model.enums.PointUseSource
import com.cresoty.catpospoint.model.interfaces.PadAction
import com.cresoty.catpospoint.model.state.ConfigState
import com.cresoty.catpospoint.model.state.CustomerState
import com.cresoty.catpospoint.model.state.PointState
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main01
import com.cresoty.catpospoint.presentation.theme.main04
import com.cresoty.catpospoint.presentation.theme.notice
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.sub01
import com.cresoty.catpospoint.presentation.theme.sub02
import com.cresoty.catpospoint.presentation.theme.success
import com.cresoty.catpospoint.presentation.theme.transparent
import com.cresoty.catpospoint.toDecimalString
import com.cresoty.catpospoint.ui.preview.TabletPreview

// ── 키 타입 ───────────────────────────────────────────────────────────────

sealed class PadKey {
    data class Number(val digit: String) : PadKey()
    object DeleteOne : PadKey()
    object DeleteAll : PadKey()
}

// ── 키 배열 (레이아웃 변경 시 이 데이터만 수정) ─────────────────────────

val NUMBER_PAD_KEYS: List<List<PadKey>> = listOf(
    listOf(PadKey.Number("1"), PadKey.Number("2"), PadKey.Number("3")),
    listOf(PadKey.Number("4"), PadKey.Number("5"), PadKey.Number("6")),
    listOf(PadKey.Number("7"), PadKey.Number("8"), PadKey.Number("9")),
    listOf(PadKey.DeleteAll, PadKey.Number("0"), PadKey.DeleteOne),
)

// ── 단일 버튼 (when 분기 한 곳) ──────────────────────────────────────────

@Composable
fun PadKeyButton(
    key: PadKey,
    onClick: (PadKey) -> Unit
) {
    ClickSoundButton(
        modifier = Modifier.size(width = 230.dpx, height = 110.dpx),
        onClick = { onClick(key) },
        backgroundColor = transparent
    ) {
        when (key) {
            is PadKey.Number -> Text(
                text = key.digit,
                fontSize = 50.spx,
                color = common02
            )

            PadKey.DeleteOne -> Image(
                modifier = Modifier.size(width = 63.dpx, height = 43.dpx),
                painter = painterResource(R.drawable.icon_delete),
                contentDescription = "한 자리 삭제"
            )

            PadKey.DeleteAll -> Text(
                text = "전체삭제",
                fontSize = 30.spx,
                color = common02
            )
        }
    }
}

// ── 그리드 레이아웃 (순수 배치만 담당) ───────────────────────────────────

@Composable
fun NumberPadGrid(
    modifier: Modifier = Modifier,
    keys: List<List<PadKey>> = NUMBER_PAD_KEYS,
    onKeyPress: (PadKey) -> Unit
) {
    Column(modifier) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                row.forEach { key ->
                    PadKeyButton(key = key, onClick = onKeyPress)
                }
            }
        }
    }
}

// ── NumberPad (이벤트 라우팅만 담당) ─────────────────────────────────────

@Composable
fun NumberPad(
    step: PointDeltaProcess,
    checkbox: Int,
    checkboxColor: Color,
    customerState: CustomerState,
    pointState: PointState,
    configState: ConfigState,
    onDispatch: (PadAction) -> Unit
) {
    val verify = customerState.verifyResult?.let {
        if (it) Triple(R.drawable.icon_confirm, "인증 성공", success)
        else Triple(R.drawable.icon_alert, "인증번호가 일치하지 않습니다.", notice)
    }

    val height = if (step is PointDeltaProcess.POINT_USE_AMOUNT_INPUT) 858f.dpx else 736f.dpx

    Column(
        modifier = Modifier.size(width = 720f.dpx, height = height),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (step is PointDeltaProcess.POINT_USE_AMOUNT_INPUT) {
            UsePointAmountField(
                pointState = pointState,
                onDispatch = onDispatch
            )
            Spacer(modifier = Modifier.size(14f.dpx))
        } else {
            MaskingNumberField(
                step = step,
                pointState = pointState,
                customerState = customerState,
                onDispatch = onDispatch
            )
            Spacer(modifier = Modifier.size(82f.dpx))
        }

        NumberPadGrid { key ->
            when (key) {
                is PadKey.Number -> onDispatch(PadAction.OnClickNumberPad(key.digit))
                PadKey.DeleteOne -> onDispatch(PadAction.OnClickDeleteLastPhoneNumber)
                PadKey.DeleteAll -> onDispatch(PadAction.OnClickDeleteAllPhoneNumber)
            }
        }

        Spacer(modifier = Modifier.size(34.dpx))

        PersonalInfoUseAgree(
            step = step,
            checkbox = checkbox,
            checkboxColor = checkboxColor,
            verify = verify,
            configState = configState,
            onClickPersonalInfoUse = { onDispatch(PadAction.OnClickPersonalInfoUse) }
        )

        Spacer(modifier = Modifier.size(7f.dpx))
    }
}

// ── 포인트 사용 금액 입력 필드 ────────────────────────────────────────────

@Composable
fun UsePointAmountField(
    pointState: PointState,
    onDispatch: (PadAction) -> Unit
) {
    val useAmount = pointState.pointDelta.toDecimalString()
    val pointBalance = pointState.pointBalance.toDecimalString()

    val buttonList = PointQuickInputType.entries
    val amount = if (useAmount.isEmpty()) "얼마인가요?" else "$useAmount P"
    val fontColor = if (useAmount.isEmpty()) sub02 else main01
    val fontWeight = if (useAmount.isEmpty()) FontWeight.Normal else FontWeight.Bold

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(294f.dpx),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "사용하실 포인트는",
            fontSize = 50f.spx,
            lineHeight = 50f.spx,
            color = main01
        )

        Text(
            text = amount,
            fontSize = 50f.spx,
            lineHeight = 50f.spx,
            fontWeight = fontWeight,
            color = fontColor
        )

        Spacer(modifier = Modifier.size(31.dpx))

        Text(
            text = "보유 포인트 ${pointBalance}P",
            fontSize = 30f.spx,
            lineHeight = 30f.spx,
            color = common01
        )

        Spacer(modifier = Modifier.size(23.dpx))

        Row(horizontalArrangement = Arrangement.spacedBy(10f.dpx)) {
            buttonList.mapIndexed { index, item ->
                val label = if (index != buttonList.lastIndex) "+ ${item.amount}" else item.amount
                ClickSoundButton(
                    modifier = Modifier.size(width = 150f.dpx, height = 60f.dpx),
                    backgroundColor = sub01,
                    onClick = { onDispatch(PadAction.OnClickAmountQuickButton(item)) },
                    shape = RoundedCornerShape(50f.dpx)
                ) {
                    Text(text = label, fontSize = 25f.spx, color = common01)
                }
            }
        }
    }
}

// ── 개인정보 동의 / 인증 결과 / 최소 포인트 안내 ─────────────────────────

@Composable
fun PersonalInfoUseAgree(
    step: PointDeltaProcess,
    checkbox: Int,
    checkboxColor: Color,
    verify: Triple<Int, String, Color>?,
    configState: ConfigState,
    onClickPersonalInfoUse: () -> Unit
) {
    Row(
        modifier = Modifier
            .size(width = 720f.dpx, height = 80f.dpx)
            .padding(bottom = 25.dpx),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (step) {
            PointDeltaProcess.POINT_SAVE_PHONE_NUM,
            is PointDeltaProcess.POINT_USE_PHONE_NUM,
            PointDeltaProcess.REQUEST_CST,
            PointDeltaProcess.REQUEST_NUM -> {
                ClickSoundButton(
                    onClick = onClickPersonalInfoUse,
                    backgroundColor = transparent,
                    showPressOverlay = false
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            modifier = Modifier.size(35f.dpx),
                            painter = painterResource(checkbox),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(17f.dpx))
                        Text(
                            text = "[필수] 개인정보 제공 동의합니다.",
                            fontSize = 30f.spx,
                            lineHeight = 30f.spx,
                            color = checkboxColor
                        )
                    }
                }
            }

            is PointDeltaProcess.POINT_USE_VERIFY_NUM -> {
                verify?.let {
                    Image(
                        modifier = Modifier.size(35f.dpx),
                        painter = painterResource(it.first),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(17f.dpx))
                    Text(
                        text = it.second,
                        color = it.third,
                        fontSize = 30f.spx,
                        lineHeight = 30f.spx
                    )
                }
            }

            is PointDeltaProcess.POINT_USE_AMOUNT_INPUT -> {
                if (configState.isMinPointEnabled) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "포인트는 최소 ${configState.minPoint.toDecimalString()}P부터 사용 가능합니다.",
                            fontSize = 30f.spx,
                            lineHeight = 30f.spx,
                            color = main01
                        )
                    }
                }
            }

            else -> {}
        }
    }
}

// ── 전화번호 / 인증번호 입력 표시 필드 ───────────────────────────────────

@Composable
fun MaskingNumberField(
    step: PointDeltaProcess,
    pointState: PointState,
    customerState: CustomerState,
    onDispatch: (PadAction) -> Unit
) {
    val isPhoneNumberType = step !is PointDeltaProcess.POINT_USE_VERIFY_NUM

    if (isPhoneNumberType) {
        val isInvalidCustomer = !customerState.isCustomerExist &&
                !customerState.isExistChecking &&
                customerState.phoneNumber.length > 10 &&
                step != PointDeltaProcess.POINT_SAVE_PHONE_NUM && step != PointDeltaProcess.REQUEST_CST && step != PointDeltaProcess.REQUEST_NUM

        PhoneNumberInputField(
            value = customerState.phoneNumber,
            isMasked = pointState.isMasking,
            onMaskToggle = { onDispatch(PadAction.OnClickMaskingToggle) },
            isRegisteredCustomer = !isInvalidCustomer
        )
    } else {
        Row(
            modifier = Modifier
                .size(width = 320f.dpx, height = 100f.dpx)
                .background(color = main04, shape = RoundedCornerShape(10f.dpx))
                .padding(horizontal = 13f.dpx, vertical = 20f.dpx),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = customerState.verifyNumber,
                fontSize = 40f.spx,
                color = common02,
                maxLines = 1
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────

@Preview(
    name = "번호 패드 - 전화번호 입력",
    device = "spec:width=800px,height=1319px,dpi=213",
    showBackground = true
)
@Composable
private fun NumberPadPhoneNumPreview() {
    CatposPointTheme {
        NumberPad(
            step = PointDeltaProcess.POINT_SAVE_PHONE_NUM,
            checkbox = R.drawable.icon_checkbox_unchecked,
            checkboxColor = notice,
            customerState = CustomerState(phoneNumber = "01012"),
            pointState = PointState(),
            configState = ConfigState(),
            onDispatch = {}
        )
    }
}

@Preview(
    name = "번호 패드 - 포인트 금액 입력",
    device = "spec:width=800px,height=1319px,dpi=213",
    showBackground = true
)
@Composable
private fun NumberPadAmountInputPreview() {
    CatposPointTheme {
        NumberPad(
            step = PointDeltaProcess.POINT_USE_AMOUNT_INPUT(PointUseSource.TERMINAL),
            checkbox = R.drawable.icon_checkbox_unchecked,
            checkboxColor = notice,
            customerState = CustomerState(),
            pointState = PointState(pointBalance = "5,000"),
            configState = ConfigState(isMinPointEnabled = true, minPoint = 1000),
            onDispatch = {}
        )
    }
}

@Preview(
    name = "키 버튼 단독",
    showBackground = true,
    widthDp = 250,
    heightDp = 120
)
@Composable
private fun PadKeyButtonPreview() {
    CatposPointTheme {
        Row {
            PadKeyButton(key = PadKey.Number("5"), onClick = {})
            PadKeyButton(key = PadKey.DeleteOne, onClick = {})
            PadKeyButton(key = PadKey.DeleteAll, onClick = {})
        }
    }
}


@TabletPreview(name = "그리드 단독")
@Composable
private fun NumberPadGridPreview() {
    CatposPointTheme {
        NumberPadGrid(onKeyPress = {})
    }
}
