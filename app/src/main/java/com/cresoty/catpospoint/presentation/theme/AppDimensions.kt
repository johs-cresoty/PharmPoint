package com.cresoty.catpospoint.presentation.theme

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

val LocalDpFactor = compositionLocalOf {
    // CatposPointTheme 없이 접근할 때(Preview 등) 시스템 디스플레이 밀도를 사용
    1f / Resources.getSystem().displayMetrics.density
}

val LocalSpFactor = compositionLocalOf {
    // CatposPointTheme 없이 접근할 때(Preview 등) 시스템 디스플레이 밀도를 사용
    1f / Resources.getSystem().displayMetrics.density
}

val Number.dpx: Dp
    @Composable
    get() = Dp(this.toFloat() * LocalDpFactor.current)

val Number.spx: TextUnit
    @Composable
    get() = TextUnit(this.toFloat() * LocalSpFactor.current, TextUnitType.Sp)
