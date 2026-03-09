package com.cresoty.catpossignpad.view.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpossignpad.R
import com.cresoty.catpossignpad.model.enums.PointDeltaProcess
import com.cresoty.catpossignpad.presentation.screen.InputCustomerPhoneNumber
import com.cresoty.catpossignpad.presentation.theme.dpx
import com.cresoty.catpossignpad.presentation.theme.main01
import com.cresoty.catpossignpad.presentation.theme.notice
import com.cresoty.catpossignpad.presentation.theme.white
import com.cresoty.catpossignpad.toDecimalString
import com.cresoty.catpossignpad.view.composable.common.ConfirmButtonField
import com.cresoty.catpossignpad.view.composable.inpunumber.InputNumberPointAmount
import com.cresoty.catpossignpad.view.composable.inpunumber.InputNumberSavePoint
import com.cresoty.catpossignpad.view.composable.inpunumber.InputNumberUsePoint
import com.cresoty.catpossignpad.view.composable.inpunumber.InputNumberVerify
import com.cresoty.catpossignpad.view.composable.inpunumber.NumberPad
import com.cresoty.catpossignpad.view.controller.LocalController

@Composable
fun RequestPointDelta(step: PointDeltaProcess) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()
    val main by controller.mainState.collectAsStateWithLifecycle()
    val point by controller.pointState.collectAsStateWithLifecycle()

    val storeName = config.storeName
    val paymentAmount = main.paymentAmount.toDecimalString()

    val isPersonalInfoUse = point.isPersonalInfoUse
    val checkbox =
        if (isPersonalInfoUse) R.drawable.icon_checkbox_checked else R.drawable.icon_checkbox_unchecked
    val checkboxColor = if (isPersonalInfoUse) main01 else notice

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white)
            .padding(top = 45f.dpx, start = 40f.dpx, end = 40f.dpx)
    ) {
        when (step) {
            PointDeltaProcess.POINT_SAVE_PHONE_NUM -> {
                InputNumberSavePoint(
                    modifier = Modifier.weight(1f),
                    storeName = storeName,
                    paymentAmount = paymentAmount,
                )
            }

            PointDeltaProcess.POINT_USE_PHONE_NUM -> {
                InputNumberUsePoint(
                    modifier = Modifier.weight(1f),
                    storeName = storeName,
                    paymentAmount = paymentAmount,
                )
            }

            PointDeltaProcess.POINT_USE_VERIFY_NUM -> {
                InputNumberVerify(
                    modifier = Modifier.weight(1f),
                    storeName = storeName,
                    paymentAmount = paymentAmount
                )
            }

            PointDeltaProcess.POINT_USE_AMOUNT_INPUT -> {
                InputNumberPointAmount(
                    modifier = Modifier.weight(1f),
                    storeName = storeName,
                    paymentAmount = paymentAmount
                )
            }

            PointDeltaProcess.CUSTOMER_PHONE_LOOKUP -> {
                InputCustomerPhoneNumber(
                    modifier = Modifier.weight(1f),
                    storeName = storeName,
                )

            }

            else -> {}
        }

        NumberPad(
            checkbox = checkbox,
            checkboxColor = checkboxColor,
            step = step,
        )

        ConfirmButtonField()
    }

}
