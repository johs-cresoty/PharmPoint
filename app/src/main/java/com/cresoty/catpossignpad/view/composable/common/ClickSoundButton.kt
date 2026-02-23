package com.cresoty.catpossignpad.view.composable.common

import android.view.SoundEffectConstants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ClickSoundButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    backgroundColor: Color,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    elevation: ButtonElevation? = null,
    useDebounceTime: Boolean = false,
    content: @Composable () -> Unit
) {
    var isEnabled by remember { mutableStateOf(true) }

    if (useDebounceTime) {
        LaunchedEffect(isEnabled) {
            delay(500)
            isEnabled = true
        }
    }

    val view = LocalView.current

    Button(
        onClick = {
            isEnabled = false
            view.playSoundEffect(SoundEffectConstants.CLICK)
            onClick()
        },
        enabled = if (useDebounceTime) isEnabled else true,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        shape = shape,
        border = border,
        elevation = elevation,
        contentPadding = contentPadding,
        content = {
            content()
        }
    )
}