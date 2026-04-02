package com.cresoty.catpospoint.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import com.cresoty.catpospoint.R
import com.cresoty.catpospoint.ui.component.PhoneMaskStrategy.HeadHalf
import com.cresoty.catpospoint.ui.component.PhoneMaskStrategy.Middle
import com.cresoty.catpospoint.ui.component.PhoneMaskStrategy.Tail
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main04
import com.cresoty.catpospoint.presentation.theme.notice
import com.cresoty.catpospoint.presentation.theme.notice_light
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.transparent

/**
 * 전화번호 마스킹 입력 표시 필드 (010-XXXX-XXXX 형태)
 *
 * @param value                현재 입력값 (raw, 예: "01012345678")
 * @param modifier             외부 Modifier
 * @param maskStrategy         마스킹 범위 ([PhoneMaskStrategy.Middle]: 가운데 4자리만, [PhoneMaskStrategy.Tail]: 뒤 8자리, [PhoneMaskStrategy.HeadHalf]: 010-**34-**78)
 * @param isMasked             마스킹 여부 (외부에서 제어 가능)
 * @param onMaskToggle         마스킹 토글 클릭 콜백 (null 이면 토글 버튼 미표시)
 * @param isRegisteredCustomer 등록된 회원 여부 (true = 등록, false = 미등록)
 */
@Composable
fun PhoneNumberInputField(
    value: String,
    modifier: Modifier = Modifier,
    maskStrategy: PhoneMaskStrategy = Middle,
    isMasked: Boolean = true,
    onMaskToggle: (() -> Unit)? = null,
    isRegisteredCustomer: Boolean = true
) {
    // ── 세그먼트 분리 ──────────────────────────────────────────
    val segments: List<String> = remember(value, isMasked, maskStrategy) {
        buildSegments(value, isMasked, '*', maskStrategy)
    }

    val iconRes: Int? = when {
        onMaskToggle == null -> null
        isMasked -> R.drawable.icon_mask_activate
        else -> R.drawable.icon_mask_deactivate
    }

    val backgroundColor = if (isRegisteredCustomer) main04 else notice_light
    val borderColor = if (isRegisteredCustomer) transparent else notice

    Row(
        modifier = modifier
            .size(width = 560f.dpx, height = 100f.dpx)
            .background(color = backgroundColor, shape = RoundedCornerShape(10.dpx))
            .then(
                if (borderColor != Color.Transparent)
                    Modifier.border(
                        width = 2f.dpx,
                        color = borderColor,
                        shape = RoundedCornerShape(10.dpx)
                    )
                else Modifier
            )
            .padding(horizontal = 13f.dpx, vertical = 20f.dpx),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {

        // ── 세그먼트 출력 ──────────────────────────────────
        segments.forEachIndexed { index, segment ->
            AnimatedContent(
                targetState = segment,
                transitionSpec = { fadeIn(tween(120)) togetherWith fadeOut(tween(80)) },
                label = "segment_$index",
            ) { text ->
                Box(
                    modifier = Modifier.size(width = 150f.dpx, height = 60f.dpx),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = text.withAsteriskBaselineShift(40.spx),
                        fontSize = 40.spx,
                        color = common02,
                    )
                }
            }

            if (index != segments.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.width(12f.dpx),
                    thickness = 2f.dpx,
                    color = common02,
                )
            }
        }


        // ── 마스킹 토글 버튼 ──────────────────────────────────
        if (iconRes != null && onMaskToggle != null) {
            Spacer(Modifier.width(8f.dpx))
            ClickSoundButton(
                onClick = onMaskToggle,
                backgroundColor = Color.Transparent,
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = if (isMasked) "번호 보기" else "번호 숨기기",
                    modifier = Modifier
                        .height(30.dpx)
                        .width(38.dpx)
                )
            }
        }
    }
}

/**
 * 전화번호 마스킹 범위
 * - [Middle]:   010-****-1234 (가운데 4자리만)
 * - [Tail]:     010-****-**** (뒤 8자리)
 * - [HeadHalf]: 010-**34-**78 (중간/뒤 각 그룹 앞 2자리)
 */
enum class PhoneMaskStrategy { Middle, Tail, HeadHalf }


/**
 * value → 세그먼트 변환 (010 / 중간4자리 / 뒤4자리)
 */
private fun buildSegments(
    value: String,
    isMasked: Boolean,
    maskChar: Char,
    maskStrategy: PhoneMaskStrategy,
): List<String> {
    val digits = value.filter { it.isDigit() }
    var cursor = 0
    return listOf(3, 4, 4).mapIndexed { segmentIndex, len ->
        val slice = digits.drop(cursor).take(len)
        cursor += len
        if (!isMasked || slice.isEmpty()) return@mapIndexed slice
        when (maskStrategy) {
            Middle -> if (segmentIndex == 1) slice.map { maskChar }.joinToString("") else slice
            Tail -> if (segmentIndex >= 1) slice.map { maskChar }.joinToString("") else slice
            HeadHalf -> if (segmentIndex >= 1) {
                // 앞 절반 마스킹: **34, **78
                val half = slice.length / 2
                slice.take(half).map { maskChar }.joinToString("") + slice.drop(half)
            } else slice
        }
    }
}

private fun String.withAsteriskBaselineShift(fontSize: TextUnit) = buildAnnotatedString {
    this@withAsteriskBaselineShift.forEach { ch ->
        if (ch == '*') {
            withStyle(SpanStyle(baselineShift = BaselineShift(-0.2f))) { append(ch) }
        } else {
            append(ch)
        }
    }
}


@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 600,
    device = "spec:width=800px,height=1319px,dpi=213",
)
@Composable
private fun PhoneNumberInputField_AllStates_Preview() {
    CatposPointTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(12f.dpx),
            modifier = Modifier.padding(16f.dpx),
        ) {
            Text("전화번호 - null일 때", fontSize = 12f.spx, color = Color.Gray)
            PhoneNumberInputField(
                value = "",
                isMasked = true,
                onMaskToggle = {},
            )

            Text("전화번호 - 마스킹 해제", fontSize = 12f.spx, color = Color.Gray)
            PhoneNumberInputField(
                value = "01012345678",
                isMasked = false,
                onMaskToggle = {},
            )

            Text("전화번호 - 가운데 4자리만 마스킹 (Middle)", fontSize = 12f.spx, color = Color.Gray)
            PhoneNumberInputField(
                value = "01012345678",
                maskStrategy = Middle,
                isMasked = true,
                onMaskToggle = {},
            )

            Text("전화번호 - 뒤 8자리 마스킹 (Tail)", fontSize = 12f.spx, color = Color.Gray)
            PhoneNumberInputField(
                value = "01012345678",
                maskStrategy = Tail,
                isMasked = true,
                onMaskToggle = {},
            )

            Text("전화번호 - 각 그룹 앞 2자리 마스킹 (HeadHalf: 010-**34-**78)", fontSize = 12f.spx, color = Color.Gray)
            PhoneNumberInputField(
                value = "01012345678",
                maskStrategy = HeadHalf,
                isMasked = true,
                onMaskToggle = {},
            )

            Text("전화번호 - 등록되지 않은 회원", fontSize = 12f.spx, color = Color.Gray)
            PhoneNumberInputField(
                value = "01012345678",
                maskStrategy = Tail,
                isMasked = true,
                onMaskToggle = {},
                isRegisteredCustomer = false
            )
        }
    }
}
