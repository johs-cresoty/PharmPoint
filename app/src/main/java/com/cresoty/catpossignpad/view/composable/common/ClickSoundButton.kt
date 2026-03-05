package com.cresoty.catpossignpad.view.composable.common

import android.view.SoundEffectConstants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
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
    enabled: Boolean = true,
    useDebounceTime: Boolean = false,
    content: @Composable () -> Unit
) {
    var isEnabled by remember { mutableStateOf(true) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val effectiveEnabled = if (useDebounceTime) isEnabled else enabled

    if (useDebounceTime) {
        LaunchedEffect(isEnabled) {
            delay(500)
            isEnabled = true
        }
    }

    val view = LocalView.current

    Box(
        modifier = modifier
            .alpha(if (effectiveEnabled) 1f else 0.4f)
            .clip(shape)
            .background(backgroundColor, shape)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = effectiveEnabled,
                onClick = {
                    if (useDebounceTime) isEnabled = false
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    onClick()
                }
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        content()
        if (isPressed && effectiveEnabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.12f))
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ClickSoundButtonPreview() {
    ClickSoundButton(
        onClick = {},
        backgroundColor = Color.Black
    ) {
        Text(
            text = "Click",
            color = Color.White
        )
    }
}

@Preview(name = "Disabled", showBackground = true)
@Composable
private fun ClickSoundButtonDisabledPreview() {
    ClickSoundButton(
        onClick = {},
        backgroundColor = Color.Gray,
        enabled = false
    ) {
        Text("Disabled")
    }
}
