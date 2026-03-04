package com.cresoty.catpossignpad.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun CatposSignpadTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        typography = CustomTypography,
        content = content
    )
}