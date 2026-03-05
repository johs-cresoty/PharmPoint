package com.cresoty.catpossignpad.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.cresoty.catpossignpad.R
import com.cresoty.catpossignpad.presentation.theme.dpx
import com.cresoty.catpossignpad.presentation.theme.spx

/**
 * 전화번호 / 일반 텍스트 마스킹 입력 표시 필드
 *
 * @param value             현재 입력값 (raw, 예: "01012345678")
 * @param modifier          외부 Modifier
 * @param mode              [PhoneNumberInputMode.Phone] or [PhoneNumberInputMode.Plain]
 * @param maskChar          마스킹 문자 (기본 '*')
 * @param isMasked          마스킹 여부 (외부에서 제어 가능)
 * @param onMaskToggle      마스킹 토글 클릭 콜백 (null 이면 토글 버튼 미표시)
 * @param backgroundColor   배경색
 * @param textColor         텍스트 색상
 * @param dividerColor      구분선 색상 (Phone 모드 전용)
 * @param fontSize          폰트 사이즈
 * @param width             컴포넌트 전체 너비
 * @param height            컴포넌트 전체 높이
 * @param maskIconRes       마스킹 ON 아이콘 리소스 id
 * @param unmaskIconRes     마스킹 OFF 아이콘 리소스 id
 * @param placeholder       값이 없을 때 표시할 텍스트 (null 이면 미표시)
 * @param placeholderColor  placeholder 색상
 */
@Composable
fun PhoneNumberInputField(
    value: String,
    modifier: Modifier = Modifier,
    mode: PhoneNumberInputMode = PhoneNumberInputMode.Phone(),
    maskChar: Char = '*',
    isMasked: Boolean = true,
    onMaskToggle: (() -> Unit)? = null,
    backgroundColor: Color = Color(0xFFF0F4FF),
    textColor: Color = Color(0xFF1A1A2E),
    dividerColor: Color = textColor,
    fontSize: TextUnit = 40f.spx,
    width: Dp = Dp.Unspecified,
    height: Dp = 100f.dpx,
    maskIconRes: Int? = null,
    unmaskIconRes: Int? = null,
    placeholder: String? = null,
    placeholderColor: Color = textColor.copy(alpha = 0.35f),
) {
    // ── 세그먼트 분리 ──────────────────────────────────────────
    val segments: List<String> = remember(value, isMasked, mode) {
        buildSegments(value, isMasked, maskChar, mode)
    }

    val isEmpty = value.isEmpty()

    // ── 아이콘 drawable ───────────────────────────────────────
    val iconRes: Int? = when {
        onMaskToggle == null -> null
        isMasked -> maskIconRes
        else -> unmaskIconRes
    }

    Row(
        modifier = modifier
            .then(if (width != Dp.Unspecified) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(height)
            .background(color = backgroundColor, shape = RoundedCornerShape(10f.dpx))
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
                    if (mode is PhoneNumberInputMode.Phone) {
                        // Phone 모드: 세그먼트별 고정 너비 Box
                        val boxWidth = if (mode.segmentBoxWidth == Dp.Unspecified) 150f.dpx else mode.segmentBoxWidth
                        val boxHeight = if (mode.segmentBoxHeight == Dp.Unspecified) 60f.dpx else mode.segmentBoxHeight
                        Box(
                            modifier = Modifier.size(
                                width = boxWidth,
                                height = boxHeight,
                            ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = text.withAsteriskBaselineShift(fontSize),
                                fontSize = fontSize,
                                color = textColor,
                            )
                        }
                    } else {
                        // Plain 모드
                        Text(
                            text = text,
                            fontSize = fontSize,
                            color = textColor,
                            maxLines = 1,
                        )
                    }
                }

                // Phone 모드: 세그먼트 사이 구분선
                if (mode is PhoneNumberInputMode.Phone && index != segments.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.width(12f.dpx),
                        thickness = 2f.dpx,
                        color = dividerColor,
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
                    modifier = Modifier.height(30.dpx).width(38.dpx)
                )
            }
        }
    }
}

// ── 입력 모드 ─────────────────────────────────────────────────────────────────

sealed class PhoneNumberInputMode {
    /**
     * 전화번호 모드: 010-****-**** 형태로 3개 세그먼트 분리 표시
     * @param segments 각 세그먼트 최대 자릿수 (기본 [3, 4, 4])
     */
    data class Phone(
        val segmentLengths: List<Int> = listOf(3, 4, 4),
        val segmentBoxWidth: Dp = Dp.Unspecified,
        val segmentBoxHeight: Dp = Dp.Unspecified,
    ) : PhoneNumberInputMode()

    /**
     * 일반 텍스트 모드: 세그먼트 구분 없이 단일 문자열로 표시
     * (비밀번호, 포인트 등)
     */
    object Plain : PhoneNumberInputMode()
}

// ── 내부 유틸 ─────────────────────────────────────────────────────────────────

/**
 * value → segments 변환
 * Phone 모드: ["010", "****", "****"]
 * Plain 모드: ["530521"] (단일 원소)
 */
private fun buildSegments(
    value: String,
    isMasked: Boolean,
    maskChar: Char,
    mode: PhoneNumberInputMode,
): List<String> {
    return when (mode) {
        is PhoneNumberInputMode.Phone -> {
            val digits = value.filter { it.isDigit() }
            var cursor = 0
            mode.segmentLengths.map { len ->
                val slice = digits.drop(cursor).take(len)
                cursor += len
                if (isMasked && slice.isNotEmpty()) {
                    // 입력된 자리만큼만 마스킹 (미입력 자리는 공백)
                    slice.map { maskChar }.joinToString("")
                } else {
                    slice
                }
            }
        }

        PhoneNumberInputMode.Plain -> {
            val display = if (isMasked) value.map { maskChar }.joinToString("") else value
            listOf(display)
        }
    }
}

/**
 * '●' 문자에 BaselineShift 적용 (기존 withAsteriskBaselineShift 대응)
 */
private fun String.withAsteriskBaselineShift(fontSize: TextUnit) = buildAnnotatedString {
    this@withAsteriskBaselineShift.forEach { ch ->
        if (ch == '●') {
            withStyle(SpanStyle(baselineShift = BaselineShift(0.15f))) { append(ch) }
        } else {
            append(ch)
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 600)
@Composable
private fun PhoneNumberInputField_Phone_Masked_Preview() {
    PhoneNumberInputField(
        value = "01012345678",
        mode = PhoneNumberInputMode.Phone(),
        isMasked = true,
        onMaskToggle = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 600)
@Composable
private fun PhoneNumberInputField_Phone_Unmasked_Preview() {
    PhoneNumberInputField(
        value = "01012345678",
        mode = PhoneNumberInputMode.Phone(),
        isMasked = false,
        onMaskToggle = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 600)
@Composable
private fun PhoneNumberInputField_Phone_Partial_Preview() {
    // 번호 일부만 입력된 상태
    PhoneNumberInputField(
        value = "0101234",
        mode = PhoneNumberInputMode.Phone(),
        isMasked = true,
        onMaskToggle = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 600)
@Composable
private fun PhoneNumberInputField_Phone_Empty_Preview() {
    // 아무것도 입력 안 된 상태 (placeholder)
    PhoneNumberInputField(
        value = "",
        mode = PhoneNumberInputMode.Phone(),
        isMasked = true,
        placeholder = "전화번호를 입력하세요",
        onMaskToggle = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 600)
@Composable
private fun PhoneNumberInputField_Plain_Masked_Preview() {
    // 비밀번호 입력 (항상 마스킹)
    PhoneNumberInputField(
        value = "530521",
        mode = PhoneNumberInputMode.Plain,
        isMasked = true,
        onMaskToggle = null,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 600)
@Composable
private fun PhoneNumberInputField_Plain_Unmasked_Preview() {
    // 포인트 입력 (마스킹 없음)
    PhoneNumberInputField(
        value = "15000",
        mode = PhoneNumberInputMode.Plain,
        isMasked = false,
        onMaskToggle = null,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 600)
@Composable
private fun PhoneNumberInputField_AllStates_Preview() {
    // 여러 상태 한눈에 보기
    Column(
        verticalArrangement = Arrangement.spacedBy(12f.dpx),
        modifier = Modifier.padding(16f.dpx),
    ) {
        Text("전화번호 - 마스킹", fontSize = 12f.spx, color = Color.Gray)
        PhoneNumberInputField(
            value = "01012345678",
            mode = PhoneNumberInputMode.Phone(),
            maskIconRes = R.drawable.icon_mask_activate,
            unmaskIconRes = R.drawable.icon_mask_deactivate,
            isMasked = true,
            onMaskToggle = {},
        )

        Text("전화번호 - 마스킹 해제", fontSize = 12f.spx, color = Color.Gray)
        PhoneNumberInputField(
            value = "01012345678",
            mode = PhoneNumberInputMode.Phone(),
            isMasked = false,
            maskIconRes = R.drawable.icon_mask_activate,
            unmaskIconRes = R.drawable.icon_mask_deactivate,
            onMaskToggle = {},
        )

        Text("전화번호 - 일부 입력", fontSize = 12f.spx, color = Color.Gray)
        PhoneNumberInputField(
            value = "0101234",
            mode = PhoneNumberInputMode.Phone(),
            isMasked = true,
            onMaskToggle = {},
        )
    }
}