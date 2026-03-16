package com.cresoty.catpospoint.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * 앱 전용 텍스트 스타일 디자인 시스템.
 *
 * 사용법: style = AppTextStyle.Button
 *
 * 새 스타일 추가:
 *   val MyStyle: TextStyle
 *       @Composable get() = TextStyle(
 *           fontFamily = NotoSansKr,
 *           fontWeight = FontWeight.Bold,
 *           fontSize = 30f.spx,
 *       )
 */
object AppTextStyle {


    /** 50,000원 결제*/
    val Title: TextStyle
        @Composable get() = TextStyle(
            fontSize = 53.spx,
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(500),
            color = common02,
            textAlign = TextAlign.Center,
        )

    /** 다나아약국 */
    val SubTitle1: TextStyle
        @Composable get() = TextStyle(
            fontSize = 45.spx,
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(400),
            color = common01,
            textAlign = TextAlign.Center,
        )

    /** 휴대폰 번호 입력하고 포인트 받아가세요. */
    val SubTitle2: TextStyle
        @Composable get() = TextStyle(
            fontSize = 30.spx,
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(400),
            color = common01,
            textAlign = TextAlign.Center,
        )

    /** 등록된 회원이 없습니다. */
    val Notice: TextStyle
        @Composable get() = TextStyle(
            fontSize = 25.spx,
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(400),
            color = notice,
            textAlign = TextAlign.Center,
        )

    /** 입력 필드 안에 미리 보여주는 안내 텍스트 / 약국명을 입력해 주세요. */
    val Placeholder: TextStyle
        @Composable get() = TextStyle(
            fontSize = 20.spx,
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(400),
            color = Color(0xFF717C84)
        )

    /** 확인. */
    val Button: TextStyle
        @Composable get() = TextStyle(
            fontSize = 35.spx,
            fontFamily = NotoSansKr,
            fontWeight = FontWeight(500),
            color = white,
            textAlign = TextAlign.Center,
        )


}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun AppTextStylePreview() {
    CatposPointTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            listOf(
                "Title" to AppTextStyle.Title,
                "SubTitle1" to AppTextStyle.SubTitle1,
                "SubTitle2" to AppTextStyle.SubTitle2,
                "Notice" to AppTextStyle.Notice,
                "Placeholder" to AppTextStyle.Placeholder,
            ).forEach { (name, style) ->
                Text(text = "$name — 가나다 ABCabc 123", style = style)
            }
        }
    }
}
