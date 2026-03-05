package com.cresoty.catpossignpad.view.composable.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.cresoty.catpossignpad.insetShadow
import com.cresoty.catpossignpad.presentation.theme.CatposSignpadTheme
import com.cresoty.catpossignpad.presentation.theme.common02
import com.cresoty.catpossignpad.presentation.theme.dpx
import com.cresoty.catpossignpad.presentation.theme.main03
import com.cresoty.catpossignpad.presentation.theme.spx
import com.cresoty.catpossignpad.presentation.theme.transparent
import com.cresoty.catpossignpad.presentation.theme.white

@Composable
fun ToggleAnimationButton(
    modifier: Modifier,
    isSelected: Boolean,     // true : 사용 / false : 미사용
    onChange: (Boolean) -> Unit
) {
    // 0f(왼쪽) ~ 1f(오른쪽)
    val target = if (isSelected) 1f else 0f
    val progress by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "seg_progress"
    )

    val shape = RoundedCornerShape(5f.dpx)
    val shadowShape = RoundedCornerShape(bottomEnd = 5f.dpx)

    BoxWithConstraints(
        modifier
            .height(55f.dpx)
            .clip(shape)
            .background(main03)
            .padding(5f.dpx)
    ) {
        // 선택 배경(thumb)
        Box(
            Modifier
                .fillMaxSize()
                .padding(0f.dpx)
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .offset { // 좌/우로 이동
                        val full = (this@BoxWithConstraints.maxWidth - 4.dp).roundToPx()
                        // half 영역만큼 이동: (전체 - padding) * 0.5
                        val travel = full / 2
                        IntOffset(x = (travel * progress).toInt(), y = 0)
                    }
                    .clip(shape)
                    .insetShadow(
                        shape = shadowShape,
                        radius = 5f.dpx,
                        start = false,
                        top = false,
                        end = true,
                        bottom = true
                    )
                    .background(white)
            )
        }

        // 버튼 2개
        Row(Modifier.fillMaxSize()) {
            SegmentButton(
                selected = isSelected,
                modifier = Modifier.weight(1f)
            ) {
                onChange(it)
            }
        }
    }

}

@Composable
private fun SegmentButton(
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (Boolean) -> Unit
) {
    val list = listOf(
        Pair("미사용", false),
        Pair("사용", true)
    )

    ClickSoundButton(
        onClick = { onClick(!selected) },
        backgroundColor = transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            list.map {
                Box(
                    modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val color = if (selected == it.second) common02 else white
                    val weight = if (selected == it.second) FontWeight.Bold else FontWeight.Normal

                    Text(
                        text = it.first,
                        color = color,
                        fontWeight = weight,
                        fontSize = 20f.spx
                    )
                }
            }
        }
    }
}


@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
fun ToggleAnimationButtonPreview() {
    CatposSignpadTheme {
        ToggleAnimationButton(
            Modifier, true, onChange = {}
        )
    }
}
