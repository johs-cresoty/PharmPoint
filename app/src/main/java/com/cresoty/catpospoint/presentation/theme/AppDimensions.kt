package com.cresoty.catpospoint.presentation.theme

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

// Figma 디자인 캔버스 기준 크기 (px). Preview spec: width=800px, height=1340px
private const val FIGMA_CANVAS_WIDTH_PX = 800f
private const val FIGMA_CANVAS_HEIGHT_PX = 1340f

val LocalDpFactor = compositionLocalOf {
    // CatposPointTheme 없이 접근할 때(Preview 등): 가로/세로 중 작은 비율로 균일 스케일
    val metrics = Resources.getSystem().displayMetrics
    val widthFactor = (metrics.widthPixels / metrics.density) / FIGMA_CANVAS_WIDTH_PX
    val heightFactor = (metrics.heightPixels / metrics.density) / FIGMA_CANVAS_HEIGHT_PX
    minOf(widthFactor, heightFactor)
}

val LocalSpFactor = compositionLocalOf {
    val metrics = Resources.getSystem().displayMetrics
    val widthFactor = (metrics.widthPixels / metrics.density) / FIGMA_CANVAS_WIDTH_PX
    val heightFactor = (metrics.heightPixels / metrics.density) / FIGMA_CANVAS_HEIGHT_PX
    minOf(widthFactor, heightFactor)
}

val Number.dpx: Dp
    @Composable
    get() = Dp(this.toFloat() * LocalDpFactor.current)

val Number.spx: TextUnit
    @Composable
    get() = TextUnit(this.toFloat() * LocalSpFactor.current, TextUnitType.Sp)
