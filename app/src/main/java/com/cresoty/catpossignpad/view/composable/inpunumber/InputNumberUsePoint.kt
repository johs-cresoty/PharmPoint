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
import com.cresoty.catpossignpad.px2dp
import com.cresoty.catpossignpad.px2sp
import com.cresoty.catpossignpad.view.composable.common.BackStepButton
import com.cresoty.catpossignpad.view.controller.LocalController
import com.cresoty.catpossignpad.view.theme.common02
import com.cresoty.catpossignpad.view.theme.common01
import com.cresoty.catpossignpad.view.theme.notice
import com.cresoty.catpossignpad.view.theme.white

@Composable
fun InputNumberUsePoint(
    modifier: Modifier,
    storeName : String,
    paymentAmount : String
) {
    val controller = LocalController.current
    val customer by controller.customerState.collectAsStateWithLifecycle()
    val isCustomerExist = customer.isCustomerExist

    Column(
        modifier = modifier.background(white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackStepButton { controller.dispatch(PadAction.OnClickPointNext(PointDeltaProcess.NONE)) }

//        Spacer(modifier = Modifier.size(27f.px2dp()))
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
            text = "휴대폰 번호 입력하고 본인 인증을 진행해 주세요.",
            fontSize = 30f.px2sp(),
            lineHeight = 30f.px2sp(),
            color = common01
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .height(90f.px2dp()),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(!isCustomerExist) {
                Image(
                    modifier = Modifier.size(height = 24f.px2dp(), width = 25f.px2dp()),
                    painter = painterResource(R.drawable.icon_alert),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.size(12f.px2dp()))

                Text(
                    text = "등록된 회원이 없습니다.",
                    fontSize = 25f.px2sp(),
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