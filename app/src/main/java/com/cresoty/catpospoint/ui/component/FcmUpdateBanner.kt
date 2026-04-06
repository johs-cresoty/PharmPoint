package com.cresoty.catpospoint.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main01
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white

@Composable
fun FcmUpdateBanner(
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dpx)
            .background(main01)
            .padding(horizontal = 24.dpx),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dpx),
    ) {
        Text(
            text = "앱 업데이트를 진행해주세요.",
            color = white,
            fontSize = 24.spx,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        ClickSoundButton(
            onClick = onAccept,
            backgroundColor = white,
            showPressOverlay = false,
            modifier = Modifier
                .height(48.dpx)
                .padding(horizontal = 4.dpx)
                .background(white, RoundedCornerShape(8.dpx))
                .padding(horizontal = 16.dpx),
        ) {
            Text(
                text = "업데이트 하기",
                color = main01,
                fontSize = 22.spx,
                fontWeight = FontWeight.SemiBold,
            )
        }
        ClickSoundButton(
            onClick = onDismiss,
            backgroundColor = white,
            showPressOverlay = false,
            modifier = Modifier
                .height(48.dpx)
                .padding(horizontal = 4.dpx)
                .background(white, RoundedCornerShape(8.dpx))
                .padding(horizontal = 16.dpx),
        ) {
            Text(
                text = "나중에 하기",
                color = main01,
                fontSize = 22.spx,
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FcmUpdateBannerPreview() {
    FcmUpdateBanner(
        onAccept = {},
        onDismiss = {}
    )
}