package com.cresoty.catpossignpad.view.composable.list.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.cresoty.catpossignpad.R
import com.cresoty.catpossignpad.model.enums.MainThemes
import com.cresoty.catpossignpad.presentation.component.ClickSoundButton
import com.cresoty.catpossignpad.presentation.theme.CatposSignpadTheme
import com.cresoty.catpossignpad.presentation.theme.common01
import com.cresoty.catpossignpad.presentation.theme.dpx
import com.cresoty.catpossignpad.presentation.theme.spx
import com.cresoty.catpossignpad.presentation.theme.transparent

@Composable
fun MainThemeItem(
    theme : MainThemes,
    drawable : Int,
    isSelected : Boolean,
    onClickItem : (MainThemes) -> Unit
) {
    val radioIcon = if(isSelected) R.drawable.icon_radiobutton_select else R.drawable.icon_radiobutton_unselect
    val shape = RoundedCornerShape(10f.dpx)

    ClickSoundButton(
        onClick = {
            onClickItem(theme)
        },
        backgroundColor = transparent
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10f.dpx)
        ) {
            Image(
                modifier = Modifier.size(158f.dpx)
                    .clip(shape),
                painter = painterResource(drawable),
                contentScale = ContentScale.Crop,
                alignment = theme.cropAlignment,
                contentDescription = null
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(5f.dpx),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(18f.dpx),
                    painter = painterResource(radioIcon),
                    contentDescription = null
                )

                Text(
                    text = theme.title,
                    fontSize = 20f.spx,
                    color = common01
                )
            }
        }
    }


}

@Preview
@Composable
fun MainThemeItemPreview() {
    CatposSignpadTheme {
        Column {
            MainThemeItem(
                theme = MainThemes.Theme_A,
                drawable = R.drawable.main_01,
                isSelected = true
            ) {}
            MainThemeItem(
                theme = MainThemes.Theme_B,
                drawable = R.drawable.main_02,
                isSelected = true
            ) {}
            MainThemeItem(
                theme = MainThemes.Theme_C,
                drawable = R.drawable.main_03,
                isSelected = true
            ) {}
            MainThemeItem(
                theme = MainThemes.Theme_D,
                drawable = R.drawable.main_04,
                isSelected = true
            ) {}
        }
    }
}
