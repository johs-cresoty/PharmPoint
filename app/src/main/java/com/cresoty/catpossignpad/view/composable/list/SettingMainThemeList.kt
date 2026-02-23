package com.cresoty.catpossignpad.view.composable.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.cresoty.catpossignpad.R
import com.cresoty.catpossignpad.model.enums.MainThemes
import com.cresoty.catpossignpad.px2dp
import com.cresoty.catpossignpad.view.composable.list.item.MainThemeItem

@Composable
fun SettingMainThemeList(
    selectedIndex : Int,
    onClickTheme : (Int) -> Unit
) {

    val lazyScrollState = rememberLazyListState()
    val list = listOf(
        Pair(MainThemes.Theme_A, R.drawable.main_01),
        Pair(MainThemes.Theme_B, R.drawable.main_02),
        Pair(MainThemes.Theme_C, R.drawable.main_03),
        Pair(MainThemes.Theme_D, R.drawable.main_04),
        Pair(MainThemes.Theme_D, R.drawable.main_05),
    )

    LazyVerticalGrid(
        horizontalArrangement = Arrangement.spacedBy(9f.px2dp()),
        verticalArrangement = Arrangement.spacedBy(25f.px2dp()),
        columns = GridCells.Fixed(3)
    ) {
        itemsIndexed(
            items = list,
            key = { index, item ->
                index
            }
        ) { index, item ->
            MainThemeItem(
                theme = item.first,
                drawable = item.second,
                isSelected = index == selectedIndex
            ) {
                onClickTheme(index)
            }

        }

    }

}