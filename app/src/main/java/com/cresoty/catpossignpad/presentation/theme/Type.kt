package com.cresoty.catpossignpad.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.cresoty.catpossignpad.R

val NotoSansKr = FontFamily(
    Font(R.font.noto_sans_kr_regular, FontWeight.Normal),
    Font(R.font.noto_sans_kr_medium, FontWeight.Medium),
    Font(R.font.noto_sans_kr_bold, FontWeight.Bold)
)

val CustomTypography = Typography().run {
    Typography(
        displayLarge = displayLarge.copy(fontFamily = NotoSansKr),
        displayMedium = displayMedium.copy(fontFamily = NotoSansKr),
        displaySmall = displaySmall.copy(fontFamily = NotoSansKr),

        headlineLarge = headlineLarge.copy(fontFamily = NotoSansKr),
        headlineMedium = headlineMedium.copy(fontFamily = NotoSansKr),
        headlineSmall = headlineSmall.copy(fontFamily = NotoSansKr),

        titleLarge = titleLarge.copy(fontFamily = NotoSansKr),
        titleMedium = titleMedium.copy(fontFamily = NotoSansKr),
        titleSmall = titleSmall.copy(fontFamily = NotoSansKr),

        bodyLarge = bodyLarge.copy(fontFamily = NotoSansKr),
        bodyMedium = bodyMedium.copy(fontFamily = NotoSansKr),
        bodySmall = bodySmall.copy(fontFamily = NotoSansKr),

        labelLarge = labelLarge.copy(fontFamily = NotoSansKr),
        labelMedium = labelMedium.copy(fontFamily = NotoSansKr),
        labelSmall = labelSmall.copy(fontFamily = NotoSansKr)
    )
}

