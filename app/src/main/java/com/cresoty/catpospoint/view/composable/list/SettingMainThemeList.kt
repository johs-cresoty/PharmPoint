package com.cresoty.catpospoint.view.composable.list

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cresoty.catpospoint.R
import com.cresoty.catpospoint.model.enums.MainThemes
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.transparent
import com.cresoty.catpospoint.view.composable.list.item.AddImageBox
import com.cresoty.catpospoint.view.composable.list.item.MainThemeItem
import com.cresoty.catpospoint.view.composable.list.item.ThemeRadioButton

@Composable
fun SettingMainThemeList(
    selectedIndex: Int,
    customImageUri: Uri?,
    onClickTheme: (Int) -> Unit,
    onCustomImageCropped: (Uri) -> Unit
) {
    val list = listOf(
        Pair(MainThemes.Theme_A, R.drawable.main_1),
        Pair(MainThemes.Theme_B, R.drawable.main_2),
        Pair(MainThemes.Theme_C , R.drawable.main_3),
        Pair(MainThemes.Theme_D, R.drawable.main_4),
        Pair(MainThemes.Theme_E, R.drawable.main_5),
    )
    val customIndex = MainThemes.Theme_CUSTOM.ordinal

    LazyVerticalGrid(
        horizontalArrangement = Arrangement.spacedBy(9f.dpx),
        verticalArrangement = Arrangement.spacedBy(25f.dpx),
        columns = GridCells.Fixed(3)
    ) {
        itemsIndexed(
            items = list,
            key = { index, _ -> index }
        ) { index, item ->
            MainThemeItem(
                theme = item.first,
                drawable = item.second,
                isSelected = index == selectedIndex
            ) {
                onClickTheme(index)
            }
        }
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(10f.dpx)
            ) {
                AddImageBox(
                    croppedImageUri = customImageUri,
                    onClickItem = { onClickTheme(customIndex) },
                    onImageCropped = { uri ->
                        onCustomImageCropped(uri)
                        if (uri == Uri.EMPTY) onClickTheme(MainThemes.Theme_A.ordinal)
                    }
                )
                // 라디오 버튼 영역도 탭 시 사용자 지정 선택
                Box(
                    modifier = Modifier
                        .background(color = transparent)
                        .clickable { onClickTheme(customIndex) }) {
                    ThemeRadioButton(
                        title = "사용자 지정",
                        isSelected = customIndex == selectedIndex,
                        showRadio = customImageUri != null
                    )
                }
            }
        }
    }
}
