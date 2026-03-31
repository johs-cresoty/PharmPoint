package com.cresoty.catpospoint.ui.component


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.cresoty.catpospoint.R
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.transparent
import com.cresoty.catpospoint.ui.preview.TabletPreview

// ── 키 타입 ───────────────────────────────────────────────────────────────

sealed class PadKey {
    data class Number(val digit: String) : PadKey()
    object DeleteOne : PadKey()
    object DeleteAll : PadKey()
}

// ── 키 배열 (레이아웃 변경 시 이 데이터만 수정) ─────────────────────────

val NUMBER_PAD_KEYS: List<List<PadKey>> = listOf(
    listOf(PadKey.Number("1"), PadKey.Number("2"), PadKey.Number("3")),
    listOf(PadKey.Number("4"), PadKey.Number("5"), PadKey.Number("6")),
    listOf(PadKey.Number("7"), PadKey.Number("8"), PadKey.Number("9")),
    listOf(PadKey.DeleteAll, PadKey.Number("0"), PadKey.DeleteOne),
)

// ── 단일 버튼 (when 분기 한 곳) ──────────────────────────────────────────

@Composable
fun PadKeyButton(
    key: PadKey,
    onClick: (PadKey) -> Unit
) {
    ClickSoundButton(
        modifier = Modifier.size(width = 230.dpx, height = 110.dpx),
        onClick = { onClick(key) },
        backgroundColor = transparent
    ) {
        when (key) {
            is PadKey.Number -> Text(
                text = key.digit,
                fontSize = 50.spx,
                color = common02
            )

            PadKey.DeleteOne -> Image(
                modifier = Modifier.size(width = 63.dpx, height = 43.dpx),
                painter = painterResource(R.drawable.icon_delete),
                contentDescription = "한 자리 삭제"
            )

            PadKey.DeleteAll -> Text(
                text = "전체삭제",
                fontSize = 30.spx,
                color = common02
            )
        }
    }
}

// ── 그리드 레이아웃 (순수 배치만 담당) ───────────────────────────────────

@Composable
fun NumberPadGrid(
    modifier: Modifier = Modifier,
    keys: List<List<PadKey>> = NUMBER_PAD_KEYS,
    onKeyPress: (PadKey) -> Unit
) {
    Column(modifier) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                row.forEach { key ->
                    PadKeyButton(key = key, onClick = onKeyPress)
                }
            }
        }
    }
}


@TabletPreview(name = "그리드 단독")
@Composable
private fun NumberPadGridPreview() {
    CatposPointTheme {
        NumberPadGrid(onKeyPress = {})
    }
}
