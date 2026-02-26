package com.cresoty.catpossignpad.view.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpossignpad.R
import com.cresoty.catpossignpad.model.Quatro
import com.cresoty.catpossignpad.model.enums.PointDeltaProcess
import com.cresoty.catpossignpad.model.interfaces.PadAction
import com.cresoty.catpossignpad.px2dp
import com.cresoty.catpossignpad.px2sp
import com.cresoty.catpossignpad.toDecimalString
import com.cresoty.catpossignpad.view.composable.common.ConfirmButtonField
import com.cresoty.catpossignpad.view.controller.LocalController
import com.cresoty.catpossignpad.view.theme.common01
import com.cresoty.catpossignpad.view.theme.common02
import com.cresoty.catpossignpad.view.theme.main01
import com.cresoty.catpossignpad.view.theme.main04
import com.cresoty.catpossignpad.view.theme.white
import kotlinx.coroutines.delay

@Composable
fun PointDeltaProcDone(
    step: PointDeltaProcess
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()
    val storeName = config.storeName
    val minAmount = config.minPoint
    val timeout = config.timeout

    val point by controller.pointState.collectAsStateWithLifecycle()
    val pointDelta = point.pointDelta
    val pointBalance = point.pointBalance

    val (image, mainMent, subMent, balanceMent) = when (step) {
        PointDeltaProcess.POINT_SAVE_PROC_DONE -> {
            Quatro(
                R.drawable.icon_point_earn_done,
                if (pointDelta == "0") "적립완료" else "${pointDelta}P 적립완료",
                "${storeName} \n포인트가 적립되었습니다.",
                "보유 포인트"
            )
        }

        PointDeltaProcess.POINT_USE_PROC_DONE -> {
            Quatro(
                R.drawable.icon_point_use_done,
                "${pointDelta}P 사용완료",
                "${storeName} \n포인트가 사용되었습니다.",
                "잔여 포인트"
            )
        }

        else -> {
            Quatro(
                R.drawable.icon_point_use_shortage,
                "포인트 부족",
                "$storeName \n최소 ${minAmount.toDecimalString()}P부터 사용 가능합니다.",
                "잔여 포인트"
            )
        }
    }

    var leftTime by remember { mutableIntStateOf(timeout) }
    LaunchedEffect(Unit) {
        while (leftTime != 0) {
            delay(1000)

            leftTime -= 1
        }
        controller.dispatch(PadAction.OnClickPointNext(PointDeltaProcess.NONE))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white)
            .padding(horizontal = 40f.px2dp()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = null
        )

        Spacer(modifier = Modifier.size(52f.px2dp()))

        Text(
            text = mainMent,
            fontSize = 53f.px2sp(),
            color = common02,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.size(23f.px2dp()))

        Text(
            text = subMent,
            fontSize = 30f.px2sp(),
            lineHeight = 30f.px2sp(),
            color = common01,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(23f.px2dp()))

        Row(
            modifier = Modifier
                .height(110f.px2dp())
                .background(color = main04, shape = RoundedCornerShape(100f.px2dp()))
                .padding(horizontal = 44f.px2dp()),
            horizontalArrangement = Arrangement.spacedBy(10f.px2dp()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = balanceMent,
                fontSize = 30f.px2sp(),
                color = main01
            )

            Text(
                text = pointBalance.toDecimalString(),
                fontSize = 40f.px2sp(),
                color = main01,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "P",
                fontSize = 35f.px2sp(),
                color = main01,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.size(104f.px2dp()))

        Text(
            text = "${leftTime}초 후 창 자동 닫힘",
            fontSize = 30f.px2sp(),
            color = common02
        )

        Spacer(modifier = Modifier.size(104f.px2dp()))

        ConfirmButtonField()
    }
}