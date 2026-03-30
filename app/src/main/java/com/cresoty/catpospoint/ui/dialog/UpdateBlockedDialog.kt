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
import com.cresoty.catpospoint.ui.component.BaseDialog
import com.cresoty.catpospoint.ui.component.ConfirmButton
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.NotoSansKr
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx

@Composable
fun UpdateBlockedDialog(
    messageTitle: String,
    message: String,
    onAcceptUpdate: () -> Unit,
) {
    BaseDialog(onDismiss = {}) {
        Box(
            modifier = Modifier
                .width(542.dpx)
                .background(Color.White, RoundedCornerShape(20f.dpx))
                .padding(18.dpx)
        ) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(18.dpx)) {

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = messageTitle,
                    color = common02,
                    fontSize = 30.spx,
                    lineHeight = 42.spx,
                    fontFamily = NotoSansKr,
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = message,
                    color = common02,
                    fontSize = 25.spx,
                    lineHeight = 30.spx,
                    fontFamily = NotoSansKr,
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Center
                )


                ConfirmButton(
                    text = "업데이트하기",
                    fontSize = 28f.spx,
                    onClick = onAcceptUpdate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84f.dpx)
                )
            }
        }
    }
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
