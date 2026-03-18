package com.cresoty.catpospoint.view.composable.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.cresoty.catpospoint.model.Penta
import com.cresoty.catpospoint.presentation.component.ClickSoundButton
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main01
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white

@Composable
fun SettingButtons(
    modifier: Modifier,
    isLogin: Boolean,
    enabled: Boolean = true,
    isSavedToastVisible: Boolean = false,
    onClickSave: () -> Unit,
    onClickClose: () -> Unit
) {
    val height = 112f.dpx

    val width = if (isLogin) 535f.dpx else 350f.dpx

    val isSaveColor = if(enabled) main01 else common01
    val list = listOf(
        Penta("닫기", 160f.dpx, onClickClose, common01, white),
        Penta(if (isLogin) "로그인" else "저장", width, onClickSave, white, isSaveColor)
    )

    val shape = RoundedCornerShape(20f.dpx)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = isSavedToastVisible,
            enter = fadeIn(tween(50)),
            exit = fadeOut(tween(50))
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "저장되었습니다.",
                color = main01,
                fontSize = 25f.spx,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12f.dpx))

        Row(
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
}
