package com.cresoty.catpossignpad.view.composable.list.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cresoty.catpossignpad.px2dp
import com.cresoty.catpossignpad.px2sp
import com.cresoty.catpossignpad.view.composable.common.ClickSoundButton
import com.cresoty.catpossignpad.view.composable.list.SettingType
import com.cresoty.catpossignpad.view.theme.common02
import com.cresoty.catpossignpad.view.theme.main03
import com.cresoty.catpossignpad.view.theme.transparent
import com.cresoty.catpossignpad.view.theme.white

@Composable
fun SettingItem(
    item : SettingType,
    isSelected : Boolean,
    onClick : () -> Unit
) {
    val backgroundColor = if(isSelected) main03 else transparent
    val shape =
        if(isSelected) RoundedCornerShape(topEnd = 10f.px2dp(), bottomEnd = 10f.px2dp())
        else RoundedCornerShape(0.dp)
    val textColor = if(isSelected) white else common02


    ClickSoundButton(
        backgroundColor = transparent,
        onClick = {
            onClick()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70f.px2dp())
                .background(color = backgroundColor, shape = shape)
                .padding(horizontal = 30f.px2dp(), vertical = 18f.px2dp()),
        ) {
            Text(
                text = item.title,
                fontSize = 25f.px2sp(),
                lineHeight = 25f.px2sp(),
                fontWeight = FontWeight.Medium,
                color = textColor
            )

        }

    }

}

@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
fun SettingDialogPreview() {
    Column {

//        SettingItem(
//            SettingType.ID_VERIFY, true
//        ) {}

        SettingItem(
            SettingType.STORE_INFO, false
        ) {}
    }

}