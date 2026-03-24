package com.cresoty.catpospoint.view.composable.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main01
import com.cresoty.catpospoint.presentation.theme.spx
import androidx.compose.ui.graphics.Color
import com.cresoty.catpospoint.presentation.theme.white
import kotlinx.coroutines.delay

/**
 * 로딩 오버레이
 *
 * [delayMs] 동안 기다린 후에도 로딩 중이면 표시.
 * [delayMs] 이내에 완료되면 오버레이가 아예 뜨지 않아 짧은 응답 시 깜빡임 없음.
 */
@Composable
fun LoadingOverlay(
    isVisible: Boolean,
    delayMs: Long = 300L,
    modifier: Modifier = Modifier
) {
    val isPreview = LocalInspectionMode.current
    var showOverlay by remember { mutableStateOf(isPreview) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(delayMs)
            showOverlay = true
        } else {
            showOverlay = false
        }
    }

    if (!showOverlay) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .pointerInput(Unit) {}, // 터치 차단
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(color = white, shape = RoundedCornerShape(20f.dpx))
                .padding(horizontal = 60f.dpx, vertical = 40f.dpx),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(72f.dpx),
                color = main01,
                strokeWidth = 5f.dpx
            )

            Spacer(modifier = Modifier.size(20f.dpx))

            Text(
                text = "조회 중입니다...",
                fontSize = 30f.spx,
                fontWeight = FontWeight.Medium,
                color = common01
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=800px,height=1319px,dpi=213")
@Composable
private fun LoadingOverlayPreview() {
    CatposPointTheme {
        LoadingOverlay(isVisible = true)
    }
}

