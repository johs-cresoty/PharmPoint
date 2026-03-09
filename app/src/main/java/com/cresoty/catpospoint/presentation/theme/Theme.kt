package com.cresoty.catpospoint.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity

@Composable
fun CatposPointTheme(
    content: @Composable () -> Unit
) {
    val factor = 1f / LocalDensity.current.density

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
