package com.cresoty.catpospoint.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpospoint.R
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.NotoSansKr
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white

@Composable
fun PointBalanceButton(
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressedBackgroundColor = if (isPressed) common02 else white
    val pressedFontColor = if (isPressed) white else common02
    val pressedIcon = if (isPressed) ColorFilter.tint(white, BlendMode.SrcIn) else ColorFilter.tint(
        common02,
        BlendMode.SrcIn
    )

    Row(
        modifier = Modifier
            .border(width = 1.dpx, color = common02, shape = RoundedCornerShape(size = 100.dpx))
            .width(250.dpx)
            .height(70.dpx)
            .background(color = pressedBackgroundColor, shape = RoundedCornerShape(size = 100.dpx))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(35.dpx)
                .padding(vertical = 2.dpx),
            painter = painterResource(R.drawable.icon_point),
            contentDescription = null,
            colorFilter = pressedIcon
        )
        Spacer(modifier = Modifier.size(10.dpx))
        Text(
            text = "포인트 조회",
            style = TextStyle(
                fontSize = 30.spx,
                fontFamily = NotoSansKr,
                fontWeight = FontWeight(400),
                color = pressedFontColor
            ),
            textAlign = TextAlign.Center,
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PointBalanceButtonPreview() {
    CatposPointTheme {
        PointBalanceButton()
    }
}
