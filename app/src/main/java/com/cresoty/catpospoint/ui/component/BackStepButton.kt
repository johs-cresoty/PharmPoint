package com.cresoty.catpospoint.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.R
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.transparent

@Composable
fun BackStepButton(
    onClickBackToMain: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        ClickSoundButton(
            backgroundColor = transparent,
            contentPadding = PaddingValues(0.dpx),  // 추가
            onClick = onClickBackToMain,
        ) {
            Image(
                modifier = Modifier.size(width = 40.dpx, height = 34.dpx),
                painter = painterResource(R.drawable.icon_backstack),
                contentDescription = null
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun BackStepButtonPreview() {
    CatposPointTheme {
        BackStepButton(
            onClickBackToMain = {}
        )
    }
}
