package com.cresoty.catpossignpad.view.composable.inpunumber

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpossignpad.R
import com.cresoty.catpossignpad.model.enums.PointDeltaProcess
import com.cresoty.catpossignpad.model.interfaces.PadAction
import com.cresoty.catpossignpad.presentation.component.BackStepButton
import com.cresoty.catpossignpad.view.controller.LocalController
import com.cresoty.catpossignpad.presentation.theme.common01
import com.cresoty.catpossignpad.presentation.theme.common02
import com.cresoty.catpossignpad.presentation.theme.notice
import com.cresoty.catpossignpad.presentation.theme.dpx
import com.cresoty.catpossignpad.presentation.theme.spx
import com.cresoty.catpossignpad.presentation.theme.white

@Composable
fun InputNumberUsePoint(
    modifier: Modifier,
    storeName: String,
    paymentAmount: String
) {
    val controller = LocalController.current
    val customer by controller.customerState.collectAsStateWithLifecycle()
    val isCustomerExist = customer.isCustomerExist
    val phoneNum = customer.phoneNumber

    Column(
        modifier = modifier.background(white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackStepButton { controller.dispatch(PadAction.OnClickPointNext(PointDeltaProcess.NONE)) }

//        Spacer(modifier = Modifier.size(27f.dpx))
        Spacer(modifier = Modifier.size(38f.dpx))

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

        Spacer(modifier = Modifier.size(24f.dpx))

        Text(
            text = "휴대폰 번호 입력하고 본인 인증을 진행해 주세요.",
            fontSize = 30f.spx,
            lineHeight = 30f.spx,
            color = common01
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90f.dpx),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isCustomerExist && !customer.isExistChecking && phoneNum.length > 10) {
                Image(
                    modifier = Modifier.size(height = 24f.dpx, width = 25f.dpx),
                    painter = painterResource(R.drawable.icon_alert),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.size(12f.dpx))

                Text(
                    text = "등록된 회원이 없습니다.",
                    fontSize = 25f.spx,
                    color = notice
                )
            }
        }

//        NumberPad(
//            checkbox = checkbox,
//            checkboxColor = checkboxColor
//        )
    }


}
