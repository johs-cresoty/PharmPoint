package com.cresoty.catpospoint.view.composable.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.cresoty.catpospoint.presentation.component.CloseButton
import com.cresoty.catpospoint.presentation.component.ConfirmButton
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx

@Composable
fun DeleteImageDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(542.dpx)
                .height(286.dpx)
                .background(Color.White, RoundedCornerShape(20.dpx))
                .padding(start = 18.dpx, end = 18.dpx, bottom = 18.dpx)
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "사용자 지정 이미지를\n삭제하시겠습니까?",
                        color = common02,
                        fontSize = 30.spx,
                        lineHeight = 42.spx,
                        fontWeight = FontWeight(500),
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dpx)
                ) {
                    CloseButton(
                        text = "아니오",
                        fontSize = 28.spx,
                        onClick = onDismiss,
                        modifier = Modifier
                            .height(84.dpx)
                            .width(160.dpx)
                    )

                    ConfirmButton(
                        text = "예",
                        fontSize = 28.spx,
                        onClick = onConfirm,
                        modifier = Modifier
                            .width(336.dpx)
                            .height(84.dpx)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DeleteImageDialogPreview() {
    CatposPointTheme {
        DeleteImageDialog(
            onConfirm = {},
            onDismiss = {}
        )
    }
}