package com.cresoty.catpossignpad.view.composable.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpossignpad.model.enums.PointDeltaProcess
import com.cresoty.catpossignpad.model.interfaces.PadAction
import com.cresoty.catpossignpad.px2dp
import com.cresoty.catpossignpad.px2sp
import com.cresoty.catpossignpad.view.controller.LocalController
import com.cresoty.catpossignpad.view.theme.common01
import com.cresoty.catpossignpad.view.theme.main01
import com.cresoty.catpossignpad.view.theme.transparent
import com.cresoty.catpossignpad.view.theme.white

@Composable
fun ConfirmButtonField() {
    val controller = LocalController.current

    val config by controller.configState.collectAsStateWithLifecycle()
    val minimum = config.minPoint

    val point by controller.pointState.collectAsStateWithLifecycle()
    val isPersonalInfoUse = point.isPersonalInfoUse
    val pointDelta = point.pointDelta

    val customer by controller.customerState.collectAsStateWithLifecycle()
    val verifyNumber = customer.verifyNumber

    val main by controller.mainState.collectAsStateWithLifecycle()
    val step = main.pointDeltaStep

    val phoneNum = customer.phoneNumber
    val isClickable = when (step) {
        PointDeltaProcess.POINT_USE_PHONE_NUM,
        PointDeltaProcess.POINT_SAVE_PHONE_NUM -> isPersonalInfoUse && phoneNum.length > 10
        PointDeltaProcess.POINT_USE_VERIFY_NUM -> verifyNumber.length > 5
        PointDeltaProcess.POINT_USE_AMOUNT_INPUT -> (pointDelta.toIntOrNull() ?: 0) >= minimum
        else -> step.isClickable
    }

    val confirmColor = if(isClickable) main01 else common01



    ClickSoundButton(
        modifier = Modifier.size(width = 720f.px2dp(), height = 112f.px2dp()),
        onClick = {
            if(isClickable) {
                when(step) {
                    PointDeltaProcess.NONE -> {}

                    //////////////////////////////////////////////////
                    // 적립
                    PointDeltaProcess.POINT_SAVE_PHONE_NUM -> {
                        controller.dispatch(PadAction.RequestSavePoint)
                    }

                    //////////////////////////////////////////////////
                    // 사용
                    PointDeltaProcess.POINT_USE_PHONE_NUM -> {
                        controller.dispatch(PadAction.RequestPointBalanceCheck)
                    }
                    PointDeltaProcess.POINT_USE_VERIFY_NUM -> {
                        controller.dispatch(PadAction.RequestCustomerVerify)
                    }
                    PointDeltaProcess.POINT_USE_AMOUNT_INPUT -> {
                        controller.dispatch(PadAction.SendToTerminalPointUse)
                    }

                    //////////////////////////////////////////////////
                    // 완료
                    PointDeltaProcess.POINT_USE_PROC_DONE,
                    PointDeltaProcess.POINT_SAVE_PROC_DONE,
                    PointDeltaProcess.POINT_USE_PROC_SHORTAGE_FAIL -> {
                        controller.dispatch(PadAction.OnClickPointNext(PointDeltaProcess.NONE))
                    }
                }
            }

        },
        shape = RoundedCornerShape(20f.px2dp()),
        backgroundColor = confirmColor
    ) {
        Text(
            text = "확인",
            fontSize = 35f.px2sp(),
            fontWeight = FontWeight.Medium,
            color = white
        )
    }

    when(step) {
        PointDeltaProcess.POINT_SAVE_PROC_DONE,
        PointDeltaProcess.POINT_USE_PROC_DONE -> {
            Spacer(modifier = Modifier.size(80f.px2dp()))
        }
        else -> {
            Box(
                modifier = Modifier.size(width = 720f.px2dp(), height = 80f.px2dp()),
                contentAlignment = Alignment.Center
            ) {
                ClickSoundButton(
                    onClick = {
                        controller.dispatch(PadAction.OnClickPointNext(PointDeltaProcess.NONE))
                    },
                    backgroundColor = transparent
                ) {
                    Text(
                        text = "다음에 하기",
                        fontSize = 25f.px2sp(),
                        color = common01
                    )
                }
            }
        }
    }

}