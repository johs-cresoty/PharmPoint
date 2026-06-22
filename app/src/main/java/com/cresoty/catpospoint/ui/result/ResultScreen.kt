package com.cresoty.catpospoint.ui.result

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.R
import com.cresoty.catpospoint.presentation.result.ResultContract
import com.cresoty.catpospoint.presentation.result.ResultStatus
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.NotoSansKr
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main01
import com.cresoty.catpospoint.presentation.theme.main04
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.sub01
import com.cresoty.catpospoint.presentation.theme.white
import com.cresoty.catpospoint.toDecimalString
import com.cresoty.catpospoint.ui.component.ConfirmButton
import kotlinx.coroutines.delay

/**
 * 상태
 * 조회 완료, 적립 완료, 사용 완료, 사용 불가
 */

@Composable
fun ResultScreen(
    state: ResultContract.State,
    sendEvent: (ResultContract.Event) -> Unit
) {
    var leftTime by remember(state.timeOut) { mutableIntStateOf(state.timeOut) }
    val imageId = when (state.status) {
        ResultStatus.FETCH_SUCCESS -> R.drawable.icon_store
        ResultStatus.EARN_SUCCESS -> R.drawable.icon_point_earn_done
        ResultStatus.USE_SUCCESS -> R.drawable.icon_point_use_done
        ResultStatus.USE_UNAVAILABLE -> R.drawable.icon_point_use_shortage
    }
    LaunchedEffect(state.timeOut) {
        while (leftTime != 0) {
            delay(1000)

            leftTime -= 1
        }
        sendEvent(ResultContract.Event.GoToWaiting)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white)
            .padding(horizontal = 40f.dpx),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.size(288.dpx))
        Image(
            modifier = Modifier
                .width(237.dpx)
                .height(159.dpx),
            painter = painterResource(imageId),
            contentDescription = null
        )

        Spacer(modifier = Modifier.size(51.dpx))

        if (state.title.isNotEmpty()) {
            Text(
                text = state.title,
                fontSize = 53.spx,
                lineHeight = 71.55.spx,
                color = common02,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.size(23.dpx))
        }

        Text(
            text = state.subTitle,
            style = TextStyle(
                fontSize = 30.spx,
                lineHeight = 40.5.spx,
                fontFamily = NotoSansKr,
                fontWeight = FontWeight(400),
                color = common01,
                textAlign = TextAlign.Center,
            )
        )


        Spacer(modifier = Modifier.size(52.dpx))

        BalancePoint(state.status, state.pointTitle, state.balancePoint, state.remainingPoint)

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "${leftTime}초 후 창 자동 닫힘",
            fontSize = 30f.spx,
            color = common02
        )

        Spacer(modifier = Modifier.size(104.dpx))
        ConfirmButton(
            modifier = Modifier.height(112.dpx),
            onClick = { sendEvent(ResultContract.Event.GoToWaiting) })
        Spacer(modifier = Modifier.size(80.dpx))
    }
}

@Composable
private fun BalancePoint(
    status: ResultStatus,
    pointTitle: String,
    balancePoint: Int = 0,
    remainingPoint: Int = 0
) {
    val disPlayPoint = if (balancePoint > 0) balancePoint else remainingPoint
    val backgroundColor = if (status == ResultStatus.USE_UNAVAILABLE) sub01 else main04
    val pointTitleColor = if (status == ResultStatus.USE_UNAVAILABLE) common01 else main01
    val pointColor = if (status == ResultStatus.USE_UNAVAILABLE) common02 else main01
    val pointWeight = if (status == ResultStatus.USE_UNAVAILABLE) 400 else 700

    Row(
        modifier = Modifier
            .height(110.dpx)
            .background(color = backgroundColor, shape = RoundedCornerShape(100f.dpx))
            .padding(horizontal = 44f.dpx),
        horizontalArrangement = Arrangement.spacedBy(10f.dpx),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = pointTitle,
            fontSize = 30.spx,
            color = pointTitleColor
        )

        Text(
            text = disPlayPoint.toDecimalString(),
            fontSize = 40.spx,
            color = pointColor,
            fontWeight = FontWeight(pointWeight)
        )

        Text(
            text = "P",
            fontSize = 35.spx,
            color = pointColor,
            fontWeight = FontWeight(pointWeight)
        )
    }
}

@Preview(name = "조회 완료", device = "spec:width=800px,height=1319px,dpi=213")
@Composable
private fun ResultScreenFetchPreview() {
    CatposPointTheme {
        ResultScreen(
            state = ResultContract.State(
                status = ResultStatus.FETCH_SUCCESS,
                subTitle = "홍길동 님",
                pointTitle = "보유 포인트",
                balancePoint = 112865,
            ),
            sendEvent = {}
        )
    }
}

@Preview(name = "적립 완료 (고객명 있음)", device = "spec:width=800px,height=1319px,dpi=213")
@Composable
private fun ResultScreenEarnWithNamePreview() {
    CatposPointTheme {
        ResultScreen(
            state = ResultContract.State(
                status = ResultStatus.EARN_SUCCESS,
                title = "1,500P 적립완료",
                subTitle = "홍길동 님",
                pointTitle = "보유 포인트",
                balancePoint = 114365,
            ),
            sendEvent = {}
        )
    }
}

@Preview(name = "적립 완료 (고객명 없음)", device = "spec:width=800px,height=1319px,dpi=213")
@Composable
private fun ResultScreenEarnNoNamePreview() {
    CatposPointTheme {
        ResultScreen(
            state = ResultContract.State(
                status = ResultStatus.EARN_SUCCESS,
                title = "1,500P 적립완료",
                subTitle = "",
                pointTitle = "보유 포인트",
                balancePoint = 1500,
            ),
            sendEvent = {}
        )
    }
}

@Preview(name = "사용 완료 (고객명 있음)", device = "spec:width=800px,height=1319px,dpi=213")
@Composable
private fun ResultScreenUseWithNamePreview() {
    CatposPointTheme {
        ResultScreen(
            state = ResultContract.State(
                status = ResultStatus.USE_SUCCESS,
                title = "5,000P 사용완료",
                subTitle = "홍길동 님",
                pointTitle = "잔여 포인트",
                remainingPoint = 107865,
            ),
            sendEvent = {}
        )
    }
}

@Preview(name = "사용 완료 (고객명 없음)", device = "spec:width=800px,height=1319px,dpi=213")
@Composable
private fun ResultScreenUseNoNamePreview() {
    CatposPointTheme {
        ResultScreen(
            state = ResultContract.State(
                status = ResultStatus.USE_SUCCESS,
                title = "5,000P 사용완료",
                subTitle = "",
                pointTitle = "잔여 포인트",
                remainingPoint = 95000,
            ),
            sendEvent = {}
        )
    }
}

@Preview(name = "사용 불가 (최소 포인트 미달)", device = "spec:width=800px,height=1319px,dpi=213")
@Composable
private fun ResultScreenUseUnavailablePreview() {
    CatposPointTheme {
        ResultScreen(
            state = ResultContract.State(
                status = ResultStatus.USE_UNAVAILABLE,
                title = "사용 불가",
                subTitle = "다나아약국\n최소 1,000P부터 사용 가능합니다.",
                pointTitle = "잔여 포인트",
                remainingPoint = 500,
            ),
            sendEvent = {}
        )
    }
}


@Preview(name = "포인트 부족", showBackground = true)
@Composable
private fun BalancePointPreview() {
    CatposPointTheme {
        Column {
            Text("적립 완료")
            BalancePoint(
                status = ResultStatus.EARN_SUCCESS,
                pointTitle = "보유 포인트",
                balancePoint = 112865
            )
            Text("사용 완료")
            BalancePoint(
                status = ResultStatus.USE_SUCCESS,
                pointTitle = "잔여 포인트",
                balancePoint = 112865
            )
            Text("사용 불가")
            BalancePoint(
                status = ResultStatus.USE_UNAVAILABLE,
                pointTitle = "잔여 포인트",
                remainingPoint = 3000
            )
        }
    }
}