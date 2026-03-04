package com.cresoty.catpossignpad.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpossignpad.R
import com.cresoty.catpossignpad.presentation.theme.CatposSignpadTheme
import com.cresoty.catpossignpad.presentation.theme.dpx
import com.cresoty.catpossignpad.presentation.theme.transparent
import com.cresoty.catpossignpad.view.composable.common.ClickSoundButton

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
            onClick = onClickBackToMain,
        ) {
            Image(
                modifier = Modifier.size(width = 40f.dpx, height = 34f.dpx),
                painter = painterResource(R.drawable.icon_backstack),
                contentDescription = null
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun BackStepButtonPreview() {
    CatposSignpadTheme {
        BackStepButton(
            onClickBackToMain = {}
        )
    }
}
