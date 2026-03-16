package com.cresoty.catpospoint.ui.component


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.R
import com.cresoty.catpospoint.presentation.theme.AppTextStyle.SubTitle2
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main01
import com.cresoty.catpospoint.presentation.theme.notice
import com.cresoty.catpospoint.presentation.theme.spx

@Composable
fun AgreeCheckBox(
    isCheckBox: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val checkbox =
        if (isCheckBox) R.drawable.icon_checkbox_checked else R.drawable.icon_checkbox_unchecked
    val checkboxColor = if (isCheckBox) main01 else notice
    Row(
        modifier = Modifier.height(80.dpx),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(35f.dpx)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onClick() },
            painter = painterResource(checkbox),
            contentDescription = null
        )

        Spacer(modifier = Modifier.size(17.dpx))

        Text(
            text = "[필수] 포인트 이용방침 동의합니다.",
            style = SubTitle2.copy(color = checkboxColor, fontSize = 30.spx),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AgreeCheckBoxPreview() {
    CatposPointTheme {
        AgreeCheckBox(true) {

        }
    }
}
