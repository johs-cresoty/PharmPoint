package com.cresoty.catpospoint.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main01
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white
import kotlinx.coroutines.delay

/**
 * 일정 시간 사용자 인터랙션이 없을 때 [onTimeout] 을 호출하는 컨테이너.
 *
 * 동작 흐름:
 * 1. 화면 어디든 터치/탭이 감지되면 타이머가 처음부터 다시 시작된다.
 * 2. 타이머가 [warningCountdownSeconds] 만 남았을 때 안내 다이얼로그 표시.
 * 3. 다이얼로그에서 카운트다운(5→4→3→2→1)을 보여주다 0이 되면 [onTimeout] 호출.
 * 4. 다이얼로그가 떠있는 동안 화면 어디든 터치하거나 "계속 사용" 버튼을 누르면
 *    다이얼로그가 닫히고 타이머가 처음부터 재시작.
 *
 * @param inactivityTimeoutSeconds 전체 대기 시간 (초). 0 이하이면 watcher 가 동작하지 않음.
 * @param warningCountdownSeconds 경고 다이얼로그 카운트다운 시간 (초). 기본 5초.
 * @param onTimeout 카운트다운이 0에 도달했을 때 호출되는 콜백.
 */
@Composable
fun InactivityTimeoutWatcher(
    inactivityTimeoutSeconds: Int,
    warningCountdownSeconds: Int = DEFAULT_WARNING_COUNTDOWN_SECONDS,
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (inactivityTimeoutSeconds <= 0) {
        Box(modifier = modifier) { content() }
        return
    }

    var interactionTick by remember { mutableIntStateOf(0) }
    var showWarning by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableIntStateOf(warningCountdownSeconds) }

    LaunchedEffect(interactionTick, inactivityTimeoutSeconds, warningCountdownSeconds) {
        showWarning = false
        val safeWarning = warningCountdownSeconds.coerceAtMost(inactivityTimeoutSeconds)
        val preWarningSeconds = inactivityTimeoutSeconds - safeWarning
        if (preWarningSeconds > 0) {
            delay(preWarningSeconds * 1000L)
        }
        showWarning = true
        for (i in safeWarning downTo 1) {
            remainingSeconds = i
            delay(1000L)
        }
        showWarning = false
        onTimeout()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(
                        requireUnconsumed = false,
                        pass = PointerEventPass.Initial,
                    )
                    interactionTick++
                }
            }
    ) {
        content()

        if (showWarning) {
            InactivityWarningDialog(
                remainingSeconds = remainingSeconds,
                onResume = { interactionTick++ },
            )
        }
    }
}

@Composable
private fun InactivityWarningDialog(
    remainingSeconds: Int,
    onResume: () -> Unit,
) {

    val noRipple = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                interactionSource = noRipple,
                indication = null,
            ) { onResume() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .width(560.dpx)
                .clip(RoundedCornerShape(20.dpx))
                .background(white)
                .clickable(
                    interactionSource = noRipple,
                    indication = null,
                ) { onResume() }
                .padding(PaddingValues(horizontal = 40.dpx, vertical = 50.dpx)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "입력하지 않으면 대기화면으로 이동됩니다.",
                fontSize = 26f.spx,
                color = common02,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(30.dpx))
            Text(
                text = remainingSeconds.toString(),
                fontSize = 90f.spx,
                color = main01,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(20.dpx))
            Text(
                text = "화면을 터치하거나 아래 버튼을 누르세요.",
                fontSize = 20f.spx,
                color = common01,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(30.dpx))
            ConfirmButton(
                text = "계속 사용",
                fontSize = 26f.spx,
                modifier = Modifier
                    .width(280.dpx)
                    .height(90.dpx),
                onClick = onResume,
            )
        }
    }

}

private const val DEFAULT_WARNING_COUNTDOWN_SECONDS = 5
