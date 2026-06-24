package com.cresoty.catpospoint.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.NotoSansKr
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.ui.component.BaseDialog
import com.cresoty.catpospoint.ui.component.ConfirmButton

/**
 * 범용 메시지 다이얼로그.
 *
 * 사용 예시:
 * - 업데이트 안내, 에러 안내, 일반 확인 다이얼로그 등 단순 메시지 + 버튼 1개 UI 공통 처리.
 *
 * UI 만 공용으로 두고, 호출자가 [title]/[message]/[confirmText]/[onConfirm] 으로 동작을 결정.
 *
 * @param title 큰 글자 타이틀 (필수)
 * @param message 본문 메시지. \n 으로 줄바꿈 가능
 * @param confirmText 확인 버튼 텍스트 (기본 "확인")
 * @param onConfirm 확인 버튼 클릭 콜백
 * @param onDismiss 다이얼로그 닫기 요청 콜백. 기본은 아무 동작 안 함 (BaseDialog 가 외부 터치/뒤로가기 차단)
 */
@Composable
fun MessageDialog(
    title: String,
    message: String,
    confirmText: String = "확인",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = {},
) {
    BaseDialog(onDismiss = onDismiss) {
        Box(
            modifier = Modifier
                .width(542.dpx)
                .background(Color.White, RoundedCornerShape(20f.dpx))
                .padding(18.dpx)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(18.dpx),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = title,
                    color = common02,
                    fontSize = 30.spx,
                    lineHeight = 42.spx,
                    fontFamily = NotoSansKr,
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Center,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = message,
                    color = common02,
                    fontSize = 25.spx,
                    lineHeight = 30.spx,
                    fontFamily = NotoSansKr,
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Center,
                )

                ConfirmButton(
                    text = confirmText,
                    fontSize = 28f.spx,
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84f.dpx),
                )
            }
        }
    }
}

@Preview(name = "MessageDialog - 업데이트 안내")
@Composable
private fun MessageDialogUpdatePreview() {
    CatposPointTheme {
        MessageDialog(
            title = "업데이트 안내",
            message = "필수 업데이트가 있습니다.",
            confirmText = "업데이트하기",
            onConfirm = {},
        )
    }
}

@Preview(name = "MessageDialog - 서버 오류")
@Composable
private fun MessageDialogErrorPreview() {
    CatposPointTheme {
        MessageDialog(
            title = "서버 오류",
            message = "일시적인 서버 오류가 발생했습니다.\n잠시 후 다시 시도해 주세요.",
            onConfirm = {},
        )
    }
}
