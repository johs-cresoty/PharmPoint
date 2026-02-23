package com.cresoty.catpossignpad.view.composable.inpunumber

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
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
import com.cresoty.catpossignpad.view.composable.common.BackStepButton
import com.cresoty.catpossignpad.view.controller.LocalController
import com.cresoty.catpossignpad.view.theme.common01
import com.cresoty.catpossignpad.view.theme.common02
import com.cresoty.catpossignpad.view.theme.white

@Composable
fun InputNumberVerify(
    modifier : Modifier,
    storeName : String,
    paymentAmount : String
) {
    val controller = LocalController.current
    val customer by controller.customerState.collectAsStateWithLifecycle()
    val isSuccess = customer.verifyResult

    Column(
        modifier = modifier.background(white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackStepButton { controller.dispatch(PadAction.OnClickPointNext(PointDeltaProcess.NONE)) }

        Spacer(modifier = Modifier.size(38f.px2dp()))

        Text(
            text = storeName,
            fontSize = 45f.px2sp(),
            lineHeight = 45f.px2sp(),
            color = common01,
            maxLines = 1
        )

        Text(
            text = "${paymentAmount}원 결제",
            fontSize = 53f.px2sp(),
            lineHeight = 53f.px2sp(),
            fontWeight = FontWeight.Medium,
            color = common02
        )

        Spacer(modifier = Modifier.size(24f.px2dp()))

        Text(
            text = "알림톡을 확인하신 후 인증 번호를 입력해 주세요.",
            fontSize = 30f.px2sp(),
            lineHeight = 30f.px2sp(),
            color = common01
        )
    }
}