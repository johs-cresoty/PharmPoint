package com.cresoty.catpospoint.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.cresoty.catpospoint.ui.component.BaseDialog
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main01
import com.cresoty.catpospoint.presentation.theme.spx

@Composable
fun UpdateRequiredDialog(progress: Int? = null) {
    BaseDialog(onDismiss = {}) {
        Column(
            modifier = Modifier
                .width(542f.dpx)
                .background(Color.White, RoundedCornerShape(20f.dpx))
                .padding(horizontal = 48f.dpx, vertical = 40f.dpx),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28f.dpx),
        ) {
            Text(
                text = "업데이트 다운로드 중입니다.\n잠시만 기다려주세요.",
                color = common02,
                fontSize = 30f.spx,
                lineHeight = 42f.spx,
                fontWeight = FontWeight(500),
                textAlign = TextAlign.Center,
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8f.dpx),
            ) {
                // 퍼센트 텍스트 (오른쪽 정렬)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        text = if (progress != null) "$progress%" else "0%",
                        color = main01,
                        fontSize = 24f.spx,
                        fontWeight = FontWeight(600),
                    )
                }

                // 진행률 바
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10f.dpx)
                        .clip(RoundedCornerShape(5f.dpx))
                ) {
                    if (progress != null) {
                        LinearProgressIndicator(
                            progress = progress / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10f.dpx),
                            color = main01,
                            backgroundColor = main01.copy(alpha = 0.15f),
                        )
                    } else {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10f.dpx),
                            color = main01,
                            backgroundColor = main01.copy(alpha = 0.15f),
                        )
                    }
                }
            }
        }
    }
}
