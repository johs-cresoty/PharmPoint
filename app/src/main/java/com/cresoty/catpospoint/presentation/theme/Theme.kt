package com.cresoty.catpospoint.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun CatposPointTheme(
    content: @Composable () -> Unit
) {
    // 가로/세로 비율 중 작은 쪽 기준으로 균일 스케일 (object-fit: contain 원리)
    // → 어떤 기기에서도 콘텐츠가 잘리지 않음
    val config = LocalConfiguration.current
    val widthFactor = config.screenWidthDp.toFloat() / 800f
    val heightFactor = config.screenHeightDp.toFloat() / 1340f
    val factor = minOf(widthFactor, heightFactor)

    CompositionLocalProvider(
        LocalDpFactor provides factor,
        LocalSpFactor provides factor
    ) {
        MaterialTheme(
            typography = CustomTypography,
            content = content
        )
    }
}
