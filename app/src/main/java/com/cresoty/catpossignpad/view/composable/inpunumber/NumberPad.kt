package com.cresoty.catpossignpad.view.composable.inpunumber

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpossignpad.R
import com.cresoty.catpossignpad.maskingPhoneNumber
import com.cresoty.catpossignpad.model.enums.PointDeltaProcess
import com.cresoty.catpossignpad.model.enums.PointQuickInputType
import com.cresoty.catpossignpad.model.interfaces.PadAction
import com.cresoty.catpossignpad.model.interfaces.ViewController
import com.cresoty.catpossignpad.model.state.ConfigState
import com.cresoty.catpossignpad.model.state.CustomerState
import com.cresoty.catpossignpad.model.state.MainState
import com.cresoty.catpossignpad.model.state.PointState
import com.cresoty.catpossignpad.model.state.PreviewState
import com.cresoty.catpossignpad.model.state.SettingState
import com.cresoty.catpossignpad.safeSubString
import com.cresoty.catpossignpad.toDecimalString
import com.cresoty.catpossignpad.presentation.component.ClickSoundButton
import com.cresoty.catpossignpad.view.controller.LocalController
import com.cresoty.catpossignpad.presentation.theme.CatposSignpadTheme
import com.cresoty.catpossignpad.presentation.theme.common01
import com.cresoty.catpossignpad.presentation.theme.common02
import com.cresoty.catpossignpad.presentation.theme.main01
import com.cresoty.catpossignpad.presentation.theme.main04
import com.cresoty.catpossignpad.presentation.theme.notice
import com.cresoty.catpossignpad.presentation.theme.notice_light
import com.cresoty.catpossignpad.presentation.theme.dpx
import com.cresoty.catpossignpad.presentation.theme.spx
import com.cresoty.catpossignpad.presentation.theme.sub01
import com.cresoty.catpossignpad.presentation.theme.sub02
import com.cresoty.catpossignpad.presentation.theme.success
import com.cresoty.catpossignpad.presentation.theme.transparent
import kotlinx.coroutines.flow.MutableStateFlow

enum class PhoneButtonType(val number: String?, val fontSize: Float) {
    NUMBER_1("1", 50f),
    NUMBER_2("2", 50f),
    NUMBER_3("3", 50f),
    NUMBER_4("4", 50f),
    NUMBER_5("5", 50f),
    NUMBER_6("6", 50f),
    NUMBER_7("7", 50f),
    NUMBER_8("8", 50f),
    NUMBER_9("9", 50f),
    DELETE_ALL("전체삭제", 30f),
    NUMBER_0("0", 50f),
    DELETE(null, 0f)
}

@Composable
fun NumberPad(
    step: PointDeltaProcess,
    checkbox: Int,
    checkboxColor: Color
) {
    val controller = LocalController.current
    val numbers = PhoneButtonType.entries

    val customer by controller.customerState.collectAsStateWithLifecycle()
    val verify = customer.verifyResult?.let {
        if (it) Triple(R.drawable.icon_confirm, "인증 성공", success)
        else Triple(R.drawable.icon_alert, "인증번호가 일치하지 않습니다.", notice)
    }

    val height = if (step == PointDeltaProcess.POINT_USE_AMOUNT_INPUT) 858f.dpx
    else 736f.dpx

    Column(
        modifier = Modifier.size(width = 720f.dpx, height = height),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (step == PointDeltaProcess.POINT_USE_AMOUNT_INPUT) {
            UsePointAmountField()

            Spacer(modifier = Modifier.size(14f.dpx))
        } else {
            MaskingNumberField(
                step = step
            )

            Spacer(modifier = Modifier.size(82f.dpx))
        }

        numbers.chunked(3).forEach { rows ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                rows.forEach { item ->
                    PhoneNumberButton(
                        item = item,
                        onClickNumber = { controller.dispatch(PadAction.OnClickNumberPad(it)) },
                        onClickDelete = { controller.dispatch(PadAction.OnClickDeleteLastPhoneNumber) },
                        onClickDeleteAll = { controller.dispatch(PadAction.OnClickDeleteAllPhoneNumber) },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(34.dpx))

        PersonalInfoUseAgree(
            step = step,
            checkbox = checkbox,
            checkboxColor = checkboxColor,
            verify = verify,
            onClickPersonalInfoUse = { controller.dispatch(PadAction.OnClickPersonalInfoUse) }
        )

        Spacer(modifier = Modifier.size(7f.dpx))

    }
}

@Composable
fun UsePointAmountField(

) {
    val controller = LocalController.current
    val point by controller.pointState.collectAsStateWithLifecycle()
    val useAmount = point.pointDelta
    val pointBalance = point.pointBalance

    val buttonList = PointQuickInputType.entries
    val amount = if (useAmount.isEmpty()) "얼마인가요?" else "$useAmount P"
//    val fontSize = if(useAmount.isEmpty()) 50f.sp(dim) else 68f.sp(dim)
    val fontColor = if (useAmount.isEmpty()) sub02 else main01
    val fontWeight = if (useAmount.isEmpty()) FontWeight.Normal else FontWeight.Bold
//    val spacerHeight = if(useAmount.isEmpty()) else 31f.dp(dim)

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
        Row(
            horizontalArrangement = Arrangement.spacedBy(10f.dpx)
        ) {
            buttonList.mapIndexed { index, item ->
                val easyInput =
                    if (index != buttonList.lastIndex) "+ ${item.amount}" else item.amount
                ClickSoundButton(
                    modifier = Modifier.size(width = 150f.dpx, height = 60f.dpx),
                    backgroundColor = sub01,
                    onClick = { controller.dispatch(PadAction.OnClickAmountQuickButton(item)) },
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

    }
}

@Composable
fun PersonalInfoUseAgree(
    step: PointDeltaProcess,
    checkbox: Int,
    checkboxColor: Color,
    verify: Triple<Int, String, Color>?,
    onClickPersonalInfoUse: () -> Unit
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()
    val minPoint = config.minPoint
    val isMinPointEnabled = config.isMinPointEnabled

    Row(
        modifier = Modifier.size(width = 720f.dpx, height = 80f.dpx).padding(bottom = 25.dpx),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (step) {
            PointDeltaProcess.POINT_SAVE_PHONE_NUM,
            PointDeltaProcess.POINT_USE_PHONE_NUM -> {
                ClickSoundButton(
                    onClick = onClickPersonalInfoUse,
                    backgroundColor = transparent,
                    showPressOverlay = false
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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

            PointDeltaProcess.POINT_USE_VERIFY_NUM -> {
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

            PointDeltaProcess.POINT_USE_AMOUNT_INPUT -> {
                if (isMinPointEnabled) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "포인트는 최소 ${minPoint.toDecimalString()}부터 사용 가능합니다.",
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

@Composable
fun PhoneNumberButton(
    item: PhoneButtonType,
    onClickNumber: (String) -> Unit,
    onClickDelete: () -> Unit,
    onClickDeleteAll: () -> Unit
) {
    val number = item.number ?: ""
    val fontSize = item.fontSize

    ClickSoundButton(
        modifier = Modifier.size(width = 230f.dpx, height = 110f.dpx),
        onClick = {
            when (item) {
                PhoneButtonType.DELETE -> onClickDelete()
                PhoneButtonType.DELETE_ALL -> onClickDeleteAll()
                else -> onClickNumber(number)
            }
        },
        backgroundColor = transparent
    ) {
        when (item) {
            PhoneButtonType.DELETE -> {
                Image(
                    modifier = Modifier.size(
                        width = 63f.dpx,
                        height = 43f.dpx
                    ),
                    painter = painterResource(R.drawable.icon_delete),
                    contentDescription = null
                )
            }

            else -> {
                Text(
                    text = number,
                    fontSize = fontSize.spx,
                    color = common02
                )
            }
        }
    }
}

/**
 * '*' 문자는 폰트 baseline 기준으로 위쪽에 그려져 숫자와 함께 표시하면 위로 떠 보임.
 * '*'에만 음수 BaselineShift를 적용해 숫자와 수직 중앙이 맞도록 보정.
 */
private fun String.withAsteriskBaselineShift(): AnnotatedString = buildAnnotatedString {
    forEach { char ->
        if (char == '*') {
            withStyle(SpanStyle(baselineShift = BaselineShift(-0.2f))) { append(char) }
        } else {
            append(char)
        }
    }
}

@Composable
fun MaskingNumberField(
    step: PointDeltaProcess
) {
    val isPhoneNumberType = step != PointDeltaProcess.POINT_USE_VERIFY_NUM

    val width = if (isPhoneNumberType) 560f.dpx else 320f.dpx
    val height = 100f.dpx

    val controller = LocalController.current
    val point by controller.pointState.collectAsStateWithLifecycle()
    val customer by controller.customerState.collectAsStateWithLifecycle()
    val backgroundColor =
        if (!customer.isCustomerExist && !customer.isExistChecking && customer.phoneNumber.length > 10 && step != PointDeltaProcess.POINT_SAVE_PHONE_NUM) notice_light else main04
    val isMasking = point.isMasking
    val drawable = if (isMasking) R.drawable.icon_mask_activate else R.drawable.icon_mask_deactivate

    val inputNumber = if (isPhoneNumberType) customer.phoneNumber else customer.verifyNumber
    val maskedList =
        if (isPhoneNumberType) {
            if (isMasking) inputNumber.maskingPhoneNumber()
            else listOf(
                inputNumber.safeSubString(0, 3),
                inputNumber.safeSubString(3, 7),
                inputNumber.safeSubString(7, 11)
            )
        } else {
            listOf(inputNumber)
        }

    Row(
        modifier = Modifier
            .size(width = width, height = height)
            .background(color = backgroundColor, shape = RoundedCornerShape(10f.dpx))
            .padding(horizontal = 13f.dpx, vertical = 20f.dpx),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        maskedList.mapIndexed { index, item ->
            if (isPhoneNumberType) {
                Box(
                    modifier = Modifier.size(width = 150f.dpx, height = 60f.dpx),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.withAsteriskBaselineShift(),
                        fontSize = 40f.spx,
                        color = common02
                    )
                }

                if (index != maskedList.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.width(12f.dpx),
                        thickness = 2f.dpx,
                        color = common02
                    )
                }
            } else {
                Text(
                    text = item,
                    fontSize = 40f.spx,
                    color = common02,
                    maxLines = 1
                )
            }

        }

        if (isPhoneNumberType) {
            ClickSoundButton(
                onClick = {
                    controller.dispatch(PadAction.OnClickMaskingToggle)
                },
                backgroundColor = transparent
            ) {
                Image(
                    painter = painterResource(drawable),
                    contentDescription = null
                )
            }
        }

    }
}

@Preview(name = "번호 패드 - 전화번호 입력", device = "spec:width=800px,height=1319px,dpi=213", showBackground = true)
@Composable
private fun NumberPadPhoneNumPreview() {
    val mockController = object : ViewController {
        override val mainState = MutableStateFlow(MainState())
        override val configState = MutableStateFlow(ConfigState())
        override val previewState = MutableStateFlow(PreviewState())
        override val settingState = MutableStateFlow(SettingState())
        override val pointState = MutableStateFlow(PointState())
        override val customerState = MutableStateFlow(CustomerState(phoneNumber = "01012"))
        override fun dispatch(action: PadAction) {}
    }
    CatposSignpadTheme {
        CompositionLocalProvider(LocalController provides mockController) {
            NumberPad(
                step = PointDeltaProcess.POINT_SAVE_PHONE_NUM,
                checkbox = R.drawable.icon_checkbox_unchecked,
                checkboxColor = notice
            )
        }
    }
}

@Preview(name = "번호 패드 - 포인트 금액 입력", device = "spec:width=800px,height=1319px,dpi=213", showBackground = true)
@Composable
private fun NumberPadAmountInputPreview() {
    val mockController = object : ViewController {
        override val mainState = MutableStateFlow(MainState())
        override val configState = MutableStateFlow(ConfigState(isMinPointEnabled = true, minPoint = 1000))
        override val previewState = MutableStateFlow(PreviewState())
        override val settingState = MutableStateFlow(SettingState())
        override val pointState = MutableStateFlow(PointState(pointBalance = "5,000"))
        override val customerState = MutableStateFlow(CustomerState())
        override fun dispatch(action: PadAction) {}
    }
    CatposSignpadTheme {
        CompositionLocalProvider(LocalController provides mockController) {
            NumberPad(
                step = PointDeltaProcess.POINT_USE_AMOUNT_INPUT,
                checkbox = R.drawable.icon_checkbox_unchecked,
                checkboxColor = notice
            )
        }
    }
}
