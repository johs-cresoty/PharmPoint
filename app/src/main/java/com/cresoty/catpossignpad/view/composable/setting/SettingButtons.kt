package com.cresoty.catpossignpad.view.composable.setting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.cresoty.catpossignpad.model.Penta
import com.cresoty.catpossignpad.presentation.component.ClickSoundButton
import com.cresoty.catpossignpad.presentation.theme.common01
import com.cresoty.catpossignpad.presentation.theme.main01
import com.cresoty.catpossignpad.presentation.theme.dpx
import com.cresoty.catpossignpad.presentation.theme.spx
import com.cresoty.catpossignpad.presentation.theme.white

@Composable
fun SettingButtons(
    modifier: Modifier,
    isLogin: Boolean,
    enabled: Boolean = true,
    onClickSave: () -> Unit,
    onClickClose: () -> Unit
) {
    val height = 112f.dpx

    val width = if (isLogin) 535f.dpx else 350f.dpx


    val list = listOf(
        Penta("닫기", 160f.dpx, onClickClose, common01, white),
        Penta(if (isLogin) "로그인" else "저장", width, onClickSave, white, main01)
    )

    val shape = RoundedCornerShape(20f.dpx)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10f.dpx),
        verticalAlignment = Alignment.Bottom
    ) {
        list.map {
            val isBorder = it.first == "닫기"
            ClickSoundButton(
                modifier = Modifier.size(width = it.second, height = height),
                enabled = if (it.first == "닫기") true else enabled,
                onClick = it.third,
                border = BorderStroke((if (isBorder) 1f else 0f).dpx, it.fourth),
                backgroundColor = it.fifth,
                shape = shape

            ) {
                Text(
                    text = it.first,
                    color = it.fourth,
                    fontWeight = FontWeight.Medium,
                    fontSize = 35f.spx
                )
            }

        }
    }
}
