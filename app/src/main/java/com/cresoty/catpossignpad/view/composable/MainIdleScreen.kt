package com.cresoty.catpossignpad.view.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpossignpad.BuildConfig
import com.cresoty.catpossignpad.R
import com.cresoty.catpossignpad.model.enums.MainThemes
import com.cresoty.catpossignpad.model.enums.PointDeltaProcess
import com.cresoty.catpossignpad.model.interfaces.PadAction
import com.cresoty.catpossignpad.model.interfaces.ViewController
import com.cresoty.catpossignpad.model.state.ConfigState
import com.cresoty.catpossignpad.model.state.CustomerState
import com.cresoty.catpossignpad.model.state.MainState
import com.cresoty.catpossignpad.model.state.PointState
import com.cresoty.catpossignpad.model.state.PreviewState
import com.cresoty.catpossignpad.model.state.SettingState
import com.cresoty.catpossignpad.px2dp
import com.cresoty.catpossignpad.px2sp
import com.cresoty.catpossignpad.view.composable.common.ClickSoundButton
import com.cresoty.catpossignpad.view.controller.LocalController
import com.cresoty.catpossignpad.view.theme.common01
import com.cresoty.catpossignpad.view.theme.common02
import com.cresoty.catpossignpad.view.theme.transparent
import com.cresoty.catpossignpad.view.theme.white
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun MainIdleScreen() {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()
    val preview by controller.previewState.collectAsStateWithLifecycle()

    val isPreview = preview.isHideDialog
    val themeIndex = if(isPreview) preview.theme ?: 0 else config.themeIndex
    val theme = MainThemes.entries[themeIndex]

    val mainScreen = when(theme) {
            MainThemes.Theme_A -> R.drawable.main_01
            MainThemes.Theme_B -> R.drawable.main_02
            MainThemes.Theme_C -> R.drawable.main_03
            MainThemes.Theme_D -> R.drawable.main_04
            MainThemes.Theme_E -> R.drawable.main_05
    }

    val preSubTitle = if(isPreview) preview.subTitle else null

    val block = mapOf<MainThemes, @Composable () -> Unit> (
            MainThemes.Theme_A to { ColumnA(preSubTitle) },
            MainThemes.Theme_B to { ColumnB(preSubTitle) },
            MainThemes.Theme_C to { ColumnC(preSubTitle) },
            MainThemes.Theme_D to { ColumnD(preSubTitle) },
            MainThemes.Theme_E to { ColumnD(preSubTitle) }
    )


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(mainScreen),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        block.getValue(theme).invoke()
    }
}

@Composable
private fun ColumnA(
    preSubTitle : String?
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 80f.px2dp(), horizontal = 40.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            Spacer(
                modifier = Modifier.size(129f.px2dp())
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = preSubTitle ?: config.subTitle,
                fontSize = 30f.px2sp(),
                fontWeight = FontWeight.Normal,
                color = common02,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.size(18f.px2dp())
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = config.storeName,
                fontSize = 80f.px2sp(),
                lineHeight = 80f.px2sp(),
                fontWeight = FontWeight.Bold,
                color = common02,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.size(70f.px2dp())
            )

            SetupButton(
                modifier = Modifier.fillMaxWidth(),
                alignment = Alignment.Center,
                color = common02
            )
        }

        PreviewCloseButton(
            theme = MainThemes.Theme_A,
            isPreview = preSubTitle == null
        )
    }
}

@Composable
private fun ColumnB(
    preSubTitle : String?
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 81f.px2dp(), horizontal = 40f.px2dp()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(width = 162f.px2dp(), height = 36f.px2dp()),
                painter = painterResource(R.drawable.logo_common02),
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.size(92f.px2dp())
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = preSubTitle ?: config.subTitle,
                fontSize = 30f.px2sp(),
                fontWeight = FontWeight.Normal,
                color = common02,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.size(18f.px2dp())
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = config.storeName,
                fontSize = 80f.px2sp(),
                lineHeight = 80f.px2sp(),
                fontWeight = FontWeight.Bold,
                color = common02,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.size(70f.px2dp())
            )


            SetupButton(
                modifier = Modifier.fillMaxWidth(),
                alignment = Alignment.Center,
                color = common01
            )
        }

        PreviewCloseButton(
            theme = MainThemes.Theme_B,
            isPreview = preSubTitle == null,
            )
    }
}

@Composable
private fun ColumnC(
    preSubTitle : String?
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 80f.px2dp(), horizontal = 40f.px2dp()),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = config.storeName,
            fontSize = 80f.px2sp(),
            lineHeight = 80f.px2sp(),
            fontWeight = FontWeight.Bold,
            color = common02,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.size(27f.px2dp())
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = preSubTitle ?: config.subTitle,
            fontSize = 30f.px2sp(),
            fontWeight = FontWeight.Normal,
            color = common02,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.size(81f.px2dp())
        )

        SetupButton(
            modifier = Modifier.fillMaxWidth(),
            alignment = Alignment.Center,
            color = common02
        )

        Spacer(
            modifier = Modifier.size(135f.px2dp())
        )

        PreviewCloseButton(
            theme = MainThemes.Theme_C,
            isPreview = preSubTitle == null
        )

    }
}

@Composable
private fun ColumnD(
    preSubTitle : String?
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 80f.px2dp(), horizontal = 40f.px2dp()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 61f.px2dp())
        ) {
            Spacer(
                modifier = Modifier.size(86f.px2dp())
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = preSubTitle ?: config.subTitle,
                fontSize = 30f.px2sp(),
                fontWeight = FontWeight.Normal,
                color = white,
                textAlign = TextAlign.Start
            )

            Spacer(
                modifier = Modifier.size(18f.px2dp())
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = config.storeName,
                fontSize = 80f.px2sp(),
                lineHeight = 80f.px2sp(),
                fontWeight = FontWeight.Bold,
                color = white,
                textAlign = TextAlign.Start
            )

            Spacer(
                modifier = Modifier.size(79f.px2dp())
            )

            SetupButton(
                modifier = Modifier.fillMaxWidth(),
                alignment = Alignment.CenterStart,
                color = white
            )
        }

        PreviewCloseButton(
            theme = MainThemes.Theme_D,
            isPreview = preSubTitle == null
        )
    }
}

@Composable
private fun SetupButton(
    modifier : Modifier,
    alignment: Alignment,
    color: Color,
) {
    val shape = RoundedCornerShape(100f.px2dp())
    val controller = LocalController.current

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        ClickSoundButton(
            modifier = Modifier.size(width = 200f.px2dp(), height = 70f.px2dp()),
            backgroundColor = transparent,
            shape = shape,
            border = BorderStroke(1.dp, color),
            onClick = {
                controller.dispatch(PadAction.OnClickSetting)
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(17f.px2dp()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(35f.px2dp()),
                    painter = painterResource(
                        when(color) {
                            common01 -> R.drawable.icon_setup_common01
                            common02 -> R.drawable.icon_setup_common02
                            else -> R.drawable.icon_setup_white
                        }
                    ),
                    contentDescription = null
                )

                Text(
                    text = "설정",
                    color = color,
                    fontSize = 30f.px2sp()
                )

            }
        }
    }
}

@Composable
private fun PreviewCloseButton(
    isPreview : Boolean,
    theme : MainThemes,
) {
    val controller = LocalController.current
    val alpha = if(theme == MainThemes.Theme_A) 0.5f else 1f
    val image = when(theme) {
        MainThemes.Theme_A,
        MainThemes.Theme_C -> R.drawable.logo_common02
        else -> R.drawable.logo_common01
    }


    if(isPreview) {
        if(theme != MainThemes.Theme_B) {
            Image(
                modifier = Modifier.size(width = 162f.px2dp(), height = 36f.px2dp())
                    .alpha(alpha)
                    .clickable {
                        if(BuildConfig.DEBUG) {
//                            controller.dispatch(PadAction.OnClickPointNext(PointDeltaProcess.POINT_USE_PHONE_NUM))
                            controller.dispatch(PadAction.RequestExpectSaveAmount)
                        }
                    },
                painter = painterResource(image),
                contentDescription = null
            )
        }
    }
    else {
        ClickSoundButton(
            modifier = Modifier.fillMaxWidth()
                .height(112f.px2dp()),
            backgroundColor = white,
            shape = RoundedCornerShape(20f.px2dp()),
            border = BorderStroke(width = 1.dp, color = common01),
            onClick = {
                controller.dispatch(PadAction.OnClickClosePreview)
            }
        ) {
            Text(
                text = "닫기",
                color = common01,
                fontSize = 35f.px2sp(),
                fontWeight = FontWeight.Medium
            )
        }
    }
}


// 미리보기
private fun previewController() = object : ViewController {
    override val mainState = MutableStateFlow(MainState())
    override val configState = MutableStateFlow(
        ConfigState(
            storeName = "미리보기 카페",
            subTitle = "포인트 적립 안내"
        )
    )
    override val previewState = MutableStateFlow(PreviewState())
    override val settingState = MutableStateFlow(SettingState())
    override val pointState = MutableStateFlow(PointState())
    override val customerState = MutableStateFlow(CustomerState())
    override fun dispatch(action: PadAction) = Unit
}

@Preview(showBackground = true)
@Composable
private fun MainIdleScreenPreview_ThemeA() {
    CompositionLocalProvider(LocalController provides previewController()) {
        MainIdleScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun MainIdleScreenPreview_ThemeB() {
    val controller = previewController().apply {
        // Theme_B로 보려면 configState의 themeIndex를 1로
    }
    CompositionLocalProvider(LocalController provides previewController()) {
        MainIdleScreen()
    }
}