package com.cresoty.catpospoint.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.presentation.theme.AppTextStyle
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white

@Composable
fun CloseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dpx),
        border = BorderStroke(1.dpx, common01),
        colors = ButtonDefaults.buttonColors(
            containerColor = white
        )
    ) {
        Text(
            text = text,
            style = AppTextStyle.Button,
            fontSize = 35.spx,
            color = common01
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CloseButtonPreview() {
    CloseButton(
        text = "닫기",
        onClick = {},
    )
}