package com.cresoty.catpospoint.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme

/**
 * 필수 업데이트 다이얼로그.
 *
 * UI 는 범용 [MessageDialog] 를 재사용하고, "업데이트하기" 버튼 클릭 시 [onAcceptUpdate] 호출.
 * 동작은 기존과 동일.
 */
@Composable
fun UpdateBlockedDialog(
    messageTitle: String,
    message: String,
    onAcceptUpdate: () -> Unit,
) {
    MessageDialog(
        title = messageTitle,
        message = message,
        confirmText = "업데이트하기",
        onConfirm = onAcceptUpdate,
    )
}


@Preview(name = "업데이트 다이얼로그")
@Composable
private fun UpdateBlockedDialogPreview() {
    CatposPointTheme {
        UpdateBlockedDialog(
            messageTitle = "업데이트 안내",
            message = "필수 업데이트가 있습니다.",
            onAcceptUpdate = {},
        )
    }
}
