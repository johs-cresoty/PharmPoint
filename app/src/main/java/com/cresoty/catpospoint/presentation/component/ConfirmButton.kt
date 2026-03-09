package com.cresoty.catpospoint.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main01
import com.cresoty.catpospoint.presentation.theme.spx

@Composable
fun ConfirmButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dpx),
        border = BorderStroke(1.dpx, common01),
        colors = ButtonDefaults.buttonColors(
            containerColor = main01,
            disabledContainerColor = common01
        )
    ) {
        Text(
            text = text,
            fontSize = 35.spx,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmButtonPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dpx),
        verticalArrangement = Arrangement.spacedBy(12.dpx)
    ) {

        ConfirmButton(
            text = "확인",
            onClick = {}
        )

        ConfirmButton(
            text = "로그인",
            onClick = {}
        )

        ConfirmButton(
            text = "저장",
            onClick = {}
        )

        ConfirmButton(
            text = "비활성 버튼",
            onClick = {},
            enabled = false
        )
    }
}