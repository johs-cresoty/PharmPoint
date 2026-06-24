package com.cresoty.catpospoint.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.main03
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white

/**
 * 초 단위 시간 옵션 중 하나를 선택하는 컴포넌트.
 * 예: [1,2,3,4,5] 또는 [10,15,20,25,30] 같은 옵션을 버튼 그룹으로 표시.
 */
@Composable
fun TimeoutOptionSelector(
    title: String,
    subTitle: String,
    options: List<Int>,
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(5f.dpx)

    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            fontSize = 20f.spx,
            fontWeight = FontWeight.Normal,
            color = common02
        )
        Text(
            text = subTitle,
            fontSize = 20f.spx,
            fontWeight = FontWeight.Normal,
            color = common01
        )
        Spacer(Modifier.size(15.dpx))
        Row(
            horizontalArrangement = Arrangement.spacedBy(5f.dpx)
        ) {
            options.forEachIndexed { index, value ->
                val fontColor = if (index == selectedIndex) white else common01
                val backgroundColor = if (index == selectedIndex) main03 else white
                val borderColor = if (index == selectedIndex) main03 else common01
                val fontWeight =
                    if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal

                ClickSoundButton(
                    modifier = Modifier.size(width = 70f.dpx, height = 55f.dpx),
                    onClick = { onSelectedChange(index) },
                    shape = shape,
                    border = BorderStroke(width = 1f.dpx, color = borderColor),
                    backgroundColor = white
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = backgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = value.toString(),
                            color = fontColor,
                            fontWeight = fontWeight
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.height(55f.dpx),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "초 후",
                    fontSize = 20f.spx,
                    fontWeight = FontWeight.Normal,
                    color = common01
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimeoutOptionSelectorPreview() {
    TimeoutOptionSelector(
        title = "화면 대기 시간",
        subTitle = "완료 화면 자동 꺼짐",
        options = listOf(1, 2, 3, 4, 5),
        selectedIndex = 2,
        onSelectedChange = {},
    )
}
