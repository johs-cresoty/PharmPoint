package com.cresoty.catpospoint.view.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.view.composable.common.BaseDialog

@Composable
fun UpdateRequiredDialog() {
    BaseDialog(onDismiss = {}) {
        Box(
            modifier = Modifier
                .width(542f.dpx)
                .height(286f.dpx)
                .background(Color.White, RoundedCornerShape(20f.dpx)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "업데이트 다운로드 중입니다.\n잠시만 기다려주세요.",
                color = common02,
                fontSize = 30f.spx,
                lineHeight = 42f.spx,
                fontWeight = FontWeight(500),
                textAlign = TextAlign.Center
            )
        }
    }
}
