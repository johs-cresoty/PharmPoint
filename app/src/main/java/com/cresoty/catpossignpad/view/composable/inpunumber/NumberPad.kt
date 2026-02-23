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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.cresoty.catpossignpad.px2dp
import com.cresoty.catpossignpad.px2sp
import com.cresoty.catpossignpad.safeSubString
import com.cresoty.catpossignpad.toDecimalString
import com.cresoty.catpossignpad.view.composable.common.ClickSoundButton
import com.cresoty.catpossignpad.view.controller.LocalController
import com.cresoty.catpossignpad.view.theme.common01
import com.cresoty.catpossignpad.view.theme.common02
import com.cresoty.catpossignpad.view.theme.main01
import com.cresoty.catpossignpad.view.theme.main04
import com.cresoty.catpossignpad.view.theme.notice
import com.cresoty.catpossignpad.view.theme.sub01
import com.cresoty.catpossignpad.view.theme.sub02
import com.cresoty.catpossignpad.view.theme.success
import com.cresoty.catpossignpad.view.theme.transparent

enum class PhoneButtonType(val number : String?, val fontSize : Float) {
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
    step : PointDeltaProcess,
    checkbox : Int,
    checkboxColor : Color
) {
    val controller = LocalController.current
    val numbers = PhoneButtonType.entries

    val customer by controller.customerState.collectAsStateWithLifecycle()
    val verify = customer.verifyResult?.let {
        if(it) Triple(R.drawable.icon_confirm, "인증 성공", success)
        else Triple(R.drawable.icon_alert, "인증번호가 일치하지 않습니다.", notice)
    }

    val height = if(step == PointDeltaProcess.POINT_USE_AMOUNT_INPUT) 858f.px2dp()
    else 691f.px2dp()

    Column(
        modifier = Modifier.size(width = 720f.px2dp(), height = height),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(step == PointDeltaProcess.POINT_USE_AMOUNT_INPUT) {
            UsePointAmountField()

            Spacer(modifier = Modifier.size(14f.px2dp()))
        }
        else {
            MaskingNumberField(
                step = step
            )

            Spacer(modifier = Modifier.size(57f.px2dp()))
        }

        numbers.chunked(3).forEach { rows ->
            Row(
                modifier = Modifier.fillMaxWidth()
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

//        Spacer(modifier = Modifier.size(24f.px2dp()))
        Spacer(modifier = Modifier.size(7f.px2dp()))

        PersonalInfoUseAgree(
            step = step,
            checkbox = checkbox,
            checkboxColor = checkboxColor,
            verify = verify,
            onClickPersonalInfoUse = { controller.dispatch(PadAction.OnClickPersonalInfoUse) }
        )

        Spacer(modifier = Modifier.size(7f.px2dp()))

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
    val amount = if(useAmount.isEmpty()) "얼마인가요?" else "$useAmount P"
//    val fontSize = if(useAmount.isEmpty()) 50f.px2sp() else 68f.px2sp()
    val fontColor = if(useAmount.isEmpty()) sub02  else main01
    val fontWeight = if(useAmount.isEmpty()) FontWeight.Normal else FontWeight.Bold
//    val spacerHeight = if(useAmount.isEmpty()) else 31f.px2dp()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(310f.px2dp())
            .padding(horizontal = 13f.px2dp(), vertical = 20f.px2dp()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "사용하실 포인트는",
            fontSize = 50f.px2sp(),
            lineHeight = 50f.px2sp(),
            color = main01
        )

        Text(
            text = amount,
            fontSize = 50f.px2sp(),
            lineHeight = 50f.px2sp(),
            fontWeight = fontWeight,
            color = fontColor
        )

        Spacer(modifier = Modifier.size(20f.px2dp()))

        Text(
            text = "보유 포인트 ${pointBalance}P",
            fontSize = 30f.px2sp(),
            lineHeight = 30f.px2sp(),
            color = common01
        )

        Spacer(modifier = Modifier.size(11f.px2dp()))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10f.px2dp())
        ) {
            buttonList.mapIndexed { index, item ->
                val easyInput = if(index != buttonList.lastIndex) "+ ${item.amount}" else item.amount
                ClickSoundButton(
                    modifier = Modifier.size(width = 150f.px2dp(), height = 60f.px2dp()),
                    backgroundColor = sub01,
                    onClick = { controller.dispatch(PadAction.OnClickAmountQuickButton(item))},
                    shape = RoundedCornerShape(50f.px2dp())
                ) {
                    Text(
                        text = easyInput,
                        fontSize = 25f.px2sp(),
                        color = common01
                    )
                }
            }
        }

    }
}

@Composable
fun PersonalInfoUseAgree(
    step : PointDeltaProcess,
    checkbox: Int,
    checkboxColor: Color,
    verify : Triple<Int, String, Color>?,
    onClickPersonalInfoUse : () -> Unit
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()
    val min = config.minPoint

    Row(
        modifier = Modifier.size(width = 720f.px2dp(), height = 80f.px2dp()),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when(step) {
            PointDeltaProcess.POINT_SAVE_PHONE_NUM,
            PointDeltaProcess.POINT_USE_PHONE_NUM -> {
                ClickSoundButton(
                    onClick = onClickPersonalInfoUse,
                    backgroundColor = transparent
                ) {
                    Image(
                        modifier = Modifier.size(35f.px2dp()),
                        painter = painterResource(checkbox),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.size(17f.px2dp()))

                    Text(
                        text = "[필수] 개인정보 제공 동의합니다.",
                        fontSize = 30f.px2sp(),
                        lineHeight = 30f.px2sp(),
                        color = checkboxColor
                    )
                }
            }
            PointDeltaProcess.POINT_USE_VERIFY_NUM -> {
                verify?.let {
                    Image(
                        modifier = Modifier.size(35f.px2dp()),
                        painter = painterResource(it.first),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.size(17f.px2dp()))

                    Text(
                        text = it.second,
                        color = it.third,
                        fontSize = 30f.px2sp(),
                        lineHeight = 30f.px2sp()
                    )
                }
            }
            PointDeltaProcess.POINT_USE_AMOUNT_INPUT -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "포인트는 최소 ${min.toDecimalString()}부터 사용 가능합니다.",
                        fontSize = 30f.px2sp(),
                        lineHeight = 30f.px2sp(),
                        color = main01
                    )
                }
            }
            else -> {}
        }
    }
}

@Composable
fun PhoneNumberButton(
    item : PhoneButtonType,
    onClickNumber : (String) -> Unit,
    onClickDelete : () -> Unit,
    onClickDeleteAll : () -> Unit
) {
    val number = item.number ?: ""
    val fontSize = item.fontSize

    ClickSoundButton(
        modifier = Modifier.size(width = 230f.px2dp(), height = 110f.px2dp()),
        onClick = {
            when(item) {
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
                        width = 63f.px2dp(),
                        height = 43f.px2dp()
                    ),
                    painter = painterResource(R.drawable.icon_delete),
                    contentDescription = null
                )
            }

            else -> {
                Text(
                    text = number,
                    fontSize = fontSize.px2sp(),
                    color = common02
                )
            }
        }
    }
}

@Composable
fun MaskingNumberField(
    step : PointDeltaProcess
) {
    val isPhoneNumberType = step != PointDeltaProcess.POINT_USE_VERIFY_NUM

    val width = if(isPhoneNumberType) 560f.px2dp() else 320f.px2dp()
    val height = 100f.px2dp()

    val controller = LocalController.current
    val point by controller.pointState.collectAsStateWithLifecycle()
    val customer by controller.customerState.collectAsStateWithLifecycle()

    val isMasking = point.isMasking
    val drawable = if(isMasking) R.drawable.icon_mask_activate else R.drawable.icon_mask_deactivate

    val inputNumber = if(isPhoneNumberType) customer.phoneNumber else customer.verifyNumber
    // TODO : *는 폰트에 따라 베이스라인 기준으로 위쪽에 그려지므로, 숫자와 함께 사용하면 하늘에 붕 떠보임.
    //        임의로 •로 변경했으나 필요시 maskingPhoneNumber() 내부 "•"를 "*"로 수정
    val maskedList =
        if(isPhoneNumberType) {
            if(isMasking) inputNumber.maskingPhoneNumber()
            else listOf(
                inputNumber.safeSubString(0, 3),
                inputNumber.safeSubString(3, 7),
                inputNumber.safeSubString(7, 11)
            )
        }
        else {
            listOf(inputNumber)
        }

    Row(
        modifier = Modifier
            .size(width = width, height = height)
            .background(color = main04, shape = RoundedCornerShape(10f.px2dp()))
            .padding(horizontal = 13f.px2dp(), vertical = 20f.px2dp()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        maskedList.mapIndexed { index, item ->
            if(isPhoneNumberType) {
                Box(
                    modifier = Modifier.size(width = 150f.px2dp(), height = 60f.px2dp()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        fontSize = 40f.px2sp(),
                        color = common02
                    )
                }

                if(index != maskedList.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.width(12f.px2dp()),
                        thickness = 2f.px2dp(),
                        color = common02
                    )
                }
            }
            else {
                Text(
                    text = item,
                    fontSize = 40f.px2sp(),
                    color = common02,
                    maxLines = 1
                )
            }

        }

        if(isPhoneNumberType) {
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
