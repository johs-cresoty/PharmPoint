package com.cresoty.catpospoint.view.composable.common

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
import com.cresoty.catpospoint.model.enums.PointDeltaProcess
import com.cresoty.catpospoint.model.interfaces.PadAction
import com.cresoty.catpospoint.presentation.component.ClickSoundButton
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main01
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.transparent
import com.cresoty.catpospoint.presentation.theme.white
import com.cresoty.catpospoint.view.controller.LocalController

@Composable
fun ConfirmButtonField() {
    val controller = LocalController.current

    val config by controller.configState.collectAsStateWithLifecycle()
    val minPoint = config.minPoint
    val isMinPointEnabled = config.isMinPointEnabled

    val point by controller.pointState.collectAsStateWithLifecycle()
    val isPersonalInfoUse = point.isPersonalInfoUse
    val pointDelta = point.pointDelta

    val customer by controller.customerState.collectAsStateWithLifecycle()
    val verifyNumber = customer.verifyNumber
    val isCustomer = customer.isCustomerExist

    val main by controller.mainState.collectAsStateWithLifecycle()
    val step = main.pointDeltaStep

    val phoneNum = customer.phoneNumber
    val isClickable = when (step) {
        PointDeltaProcess.POINT_USE_PHONE_NUM ->
            isCustomer && !customer.isExistChecking && isPersonalInfoUse && phoneNum.length > 10

        PointDeltaProcess.POINT_SAVE_PHONE_NUM, PointDeltaProcess.CUSTOMER_PHONE_LOOKUP -> isPersonalInfoUse && phoneNum.length > 10
        PointDeltaProcess.POINT_USE_VERIFY_NUM -> verifyNumber.length > 5
        PointDeltaProcess.POINT_USE_AMOUNT_INPUT -> {
            val amount = pointDelta.toIntOrNull() ?: 0
            amount > 0 && if (isMinPointEnabled) amount >= minPoint else true
        }

        else -> step.isClickable
    }

    val confirmColor = if (isClickable) main01 else common01



    ClickSoundButton(
        modifier = Modifier.size(width = 720f.dpx, height = 112.dpx),
        onClick = {
            if (isClickable) {
                when (step) {
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

                    // 휴대폰 번호 조회
                    PointDeltaProcess.CUSTOMER_PHONE_LOOKUP -> {
                        controller.dispatch(PadAction.SendToCATCustomerInfo)
                    }
                }
            }

        },
        shape = RoundedCornerShape(20f.dpx),
        backgroundColor = confirmColor
    ) {
        Text(
            text = "확인",
            fontSize = 35.spx,
            fontWeight = FontWeight.Medium,
            color = white
        )
    }

    when (step) {
        PointDeltaProcess.POINT_SAVE_PROC_DONE,
        PointDeltaProcess.POINT_USE_PROC_DONE -> {
            Spacer(modifier = Modifier.size(80f.dpx))
        }

        else -> {
            Box(
                modifier = Modifier.size(width = 720f.dpx, height = 80f.dpx),
                contentAlignment = Alignment.Center
            ) {
                ClickSoundButton(
                    showPressOverlay = false,
                    onClick = {
                        if (step == PointDeltaProcess.CUSTOMER_PHONE_LOOKUP) {
                            controller.dispatch(PadAction.SendCATFail)
                        } else {
                            controller.dispatch(PadAction.OnClickPointNext(PointDeltaProcess.NONE))
                        }
                    },
                    backgroundColor = transparent
                ) {
                    Text(
                        text = "다음에 하기",
                        fontSize = 25f.spx,
                        color = common01
                    )
                }
            }
        }
    }

}
