package com.cresoty.catpospoint.view.composable.list.item

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.cresoty.catpospoint.R
import com.cresoty.catpospoint.model.enums.MainThemes
import com.cresoty.catpospoint.presentation.component.ClickSoundButton
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.sub01
import com.cresoty.catpospoint.presentation.theme.transparent
import com.cresoty.catpospoint.presentation.theme.white
import com.cresoty.catpospoint.view.composable.common.MessageDialog
import com.yalantis.ucrop.UCrop
import java.io.File

// 태블릿 화면 비율 (800 × 1319 px 기준)
private const val TABLET_CROP_WIDTH = 800f
private const val TABLET_CROP_HEIGHT = 1319f


@Composable
fun MainThemeItem(
    theme: MainThemes,
    drawable: Int,
    isSelected: Boolean,
    onClickItem: (MainThemes) -> Unit
) {
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
                modifier = Modifier
                    .size(158f.dpx)
                    .clip(shape),
                painter = painterResource(drawable),
                contentScale = ContentScale.Crop,
                alignment = theme.cropAlignment,
                contentDescription = null
            )
            ThemeRadioButton(
                title = theme.title,
                isSelected = isSelected
            )
        }
    }
}

@Composable
fun ThemeRadioButton(
    title: String,
    isSelected: Boolean,
    showRadio: Boolean = true,
) {
    val radioIcon =
        if (isSelected) R.drawable.icon_radiobutton_select else R.drawable.icon_radiobutton_unselect
    Row(
        horizontalArrangement = Arrangement.spacedBy(5f.dpx),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showRadio) {
            Image(
                modifier = Modifier.size(18f.dpx),
                painter = painterResource(radioIcon),
                contentDescription = null
            )
        }

        Text(
            text = title,
            fontSize = 20f.spx,
            color = common01
        )
    }
}

@Composable
fun AddImageBox(
    modifier: Modifier = Modifier,
    croppedImageUri: Uri? = null,
    onClickItem: () -> Unit = {},
    onImageCropped: (Uri) -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        MessageDialog(
            onConfirm = {
                onImageCropped(Uri.EMPTY)
                showDialog = false
                // 이미지 삭제 처리
            },
            onDismiss = {
                showDialog = false
            }
        )
    }

    val context = LocalContext.current
    val shape = RoundedCornerShape(12f.dpx)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressedColor = if (isPressed) common01 else Color(0xFFA4B0B8)
    // 2단계: UCrop 결과 수신 → 부모로 URI 전달
    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            UCrop.getOutput(result.data!!)?.let { onImageCropped(it) }
        }
    }

    // 1단계: 갤러리에서 이미지 선택 → UCrop 실행
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val destFile = File(context.filesDir, "custom_theme_${System.currentTimeMillis()}.jpg")
            val cropIntent = UCrop.of(uri, Uri.fromFile(destFile))
                .withAspectRatio(TABLET_CROP_WIDTH, TABLET_CROP_HEIGHT)
                .withMaxResultSize(TABLET_CROP_WIDTH.toInt(), TABLET_CROP_HEIGHT.toInt())
                .getIntent(context)
            cropLauncher.launch(cropIntent)
        }
    }

    Box(
        modifier = modifier
            .size(158f.dpx)
            .border(
                width = 1f.dpx,
                color = pressedColor,
                shape = shape
            )
            .background(color = sub01, shape = shape)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClickItem()
                // 이미지가 없을 때만 갤러리 열기, 있으면 라디오 선택만
                if (croppedImageUri == null) {
                    pickerLauncher.launch("image/*")
                }
            },
        contentAlignment = Alignment.Center
    ) {

        if (croppedImageUri != null) {
            // 크롭 완료 후: Center Crop으로 이미지 표시
            AsyncImage(
                model = croppedImageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            ImageRemoveButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dpx)
            ) {
                showDialog = true
//                onImageCropped(Uri.EMPTY)
            }
        } else {
            // 이미지 미선택 상태: 플러스 아이콘
            Icon(
                painter = painterResource(R.drawable.icon_plus),
                contentDescription = "add",
                tint = pressedColor,
                modifier = Modifier.size(28f.dpx)
            )
        }
    }
}

@Composable
fun ImageRemoveButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressedColor = if (isPressed) white else Color(0xB2FFFFFF)
    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dpx,
                spotColor = Color(0x1A000000),
                ambientColor = Color(0x1A000000)
            )
            .size(32.dpx)
            .clip(CircleShape)
            .background(pressedColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_x),
            contentDescription = "remove",
            tint = Color.Gray,
            modifier = Modifier.size(16.dpx)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainThemeItemPreview() {
    CatposPointTheme {
        Column {
            MainThemeItem(
                theme = MainThemes.Theme_A,
                drawable = R.drawable.main_1,
                isSelected = true
            ) {}
            MainThemeItem(
                theme = MainThemes.Theme_B,
                drawable = R.drawable.main_2,
                isSelected = true
            ) {}
            MainThemeItem(
                theme = MainThemes.Theme_C,
                drawable = R.drawable.main_3,
                isSelected = true
            ) {}
            MainThemeItem(
                theme = MainThemes.Theme_D,
                drawable = R.drawable.main_4,
                isSelected = true
            ) {}
            MainThemeItem(
                theme = MainThemes.Theme_E,
                drawable = R.drawable.main_5,
                isSelected = true
            ) {}
            AddImageBox()
            ThemeRadioButton(
                title = "사용자 지정",
                isSelected = false
            )
            AddImageBox(
                croppedImageUri = "android.resource://com.cresoty.catpospoint/${R.drawable.main_3}".toUri()
            )
            ThemeRadioButton(
                title = "사용자 지정",
                isSelected = false
            )

        }
    }
}
