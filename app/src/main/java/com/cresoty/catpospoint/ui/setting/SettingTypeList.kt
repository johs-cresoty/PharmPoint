package com.cresoty.catpospoint.ui.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class SettingType(val title: String) {
    STORE_INFO("약국정보"),
//    ID_VERIFY("본인인증"),
    POINT_USE("포인트설정"),
    SCREEN_TIMEOUT("화면대기"),
    THEME("테마설정")
}

@Composable
fun SettingTypeList(
    modifier: Modifier,
    selectedIndex : Int,
    onClickSettingType: (index: Int, type : SettingType) -> Unit
) {
    val lazyColumnState = rememberLazyListState()

    val settingList = listOf(
        SettingType.STORE_INFO,
//        SettingType.ID_VERIFY,
        SettingType.POINT_USE,
        SettingType.SCREEN_TIMEOUT,
        SettingType.THEME
    )

    LazyColumn(
        state = lazyColumnState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        itemsIndexed(
            items = settingList,
            key = { index, item ->
                index
            }
        ) { index, item ->
            val isSelected = index == selectedIndex

            SettingItem(
                item = item,
                isSelected = isSelected,
                onClick = {
                    onClickSettingType(index, settingList[index])
                }
            )

        }
    }



}
