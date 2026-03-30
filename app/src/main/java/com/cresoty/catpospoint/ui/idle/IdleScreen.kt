package com.cresoty.catpospoint.ui.idle


import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.cresoty.catpospoint.R
import com.cresoty.catpospoint.model.enums.MainThemes
import com.cresoty.catpospoint.model.interfaces.ViewController
import com.cresoty.catpospoint.model.state.ConfigState
import com.cresoty.catpospoint.model.state.CustomerState
import com.cresoty.catpospoint.model.state.MainState
import com.cresoty.catpospoint.model.state.PointState
import com.cresoty.catpospoint.model.state.PreviewState
import com.cresoty.catpospoint.model.state.SettingState
import com.cresoty.catpospoint.ui.component.ClickSoundButton
import com.cresoty.catpospoint.ui.component.PointBalanceButton
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.presentation.theme.NotoSansKr
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white
import com.cresoty.catpospoint.view.controller.LocalController
import com.cresoty.catpospoint.model.event.AppEvent
import com.cresoty.catpospoint.presentation.idle.IdleContract
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun IdleScreen(
    state: IdleContract.State,
    sendEvent: (IdleContract.Event) -> Unit) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()
    val preview by controller.previewState.collectAsStateWithLifecycle()

    val themeIndex = if (state.isPreview) preview.theme ?: 0 else config.themeIndex
    val theme = MainThemes.entries.getOrElse(themeIndex) { MainThemes.Theme_CUSTOM }
    val isCustomTheme = theme == MainThemes.Theme_CUSTOM
    // 커스텀 이미지 URI: 프리뷰 모드면 preview에서, 아니면 저장된 config에서 로드
    val customImageUri: Uri? = when {
        state.isPreview -> preview.customImageUri
        isCustomTheme -> config.customImageUri.takeIf { it.isNotEmpty() }?.let { Uri.parse(it) }
        else -> null
    }

    val mainScreen: Int? = when (theme) {
        MainThemes.Theme_A -> R.drawable.main_1
        MainThemes.Theme_B -> R.drawable.main_2
        MainThemes.Theme_C -> R.drawable.main_3
        MainThemes.Theme_D -> R.drawable.main_4
        MainThemes.Theme_E -> R.drawable.main_5
        MainThemes.Theme_CUSTOM -> null
    }

    val preSubTitle = if (state.isPreview) preview.subTitle else null

    val block = mapOf<MainThemes, @Composable () -> Unit>(
        MainThemes.Theme_A to { ColumnA(preSubTitle, state.isPreview, sendEvent) },
        MainThemes.Theme_B to { ColumnB(preSubTitle, state.isPreview, sendEvent) },
        MainThemes.Theme_C to { ColumnC(preSubTitle, state.isPreview, sendEvent) },
        MainThemes.Theme_D to { ColumnD(preSubTitle, state.isPreview, sendEvent) },
        MainThemes.Theme_E to { ColumnE(preSubTitle, state.isPreview, sendEvent) },
        MainThemes.Theme_CUSTOM to { CustomPreview(preSubTitle, state.isPreview, sendEvent) }
    )


    val tapTimes = remember { mutableListOf<Long>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    val now = System.currentTimeMillis()
                    tapTimes.removeAll { now - it > 1000 }
                    tapTimes.add(now)
                    if (tapTimes.size >= 3) {
                        tapTimes.clear()
                        sendEvent(IdleContract.Event.OnTripleTap)
                    }
                }
            }
    ) {
        if (isCustomTheme && customImageUri != null) {
            AsyncImage(
                model = customImageUri,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        } else if (mainScreen != null) {
            Image(
                painter = painterResource(mainScreen),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        }

        block.getValue(theme).invoke()
    }
}

@Composable
private fun ColumnC(
    preSubTitle: String?,
    isPreview: Boolean,
    sendEvent: (IdleContract.Event) -> Unit,
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 80f.dpx, horizontal = 40.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier.size(129f.dpx)
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = preSubTitle ?: config.subTitle,
                fontSize = 30f.spx,
                lineHeight = 40.5.spx,
                fontWeight = FontWeight.Normal,
                color = common02,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(18.dpx))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = config.storeName,
                fontSize = 80f.spx,
                lineHeight = 80f.spx,
                fontWeight = FontWeight.Bold,
                color = common02,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(60.dpx))
            PointBalanceButton(onClick = if (isPreview) {{}} else { { sendEvent(IdleContract.Event.OnClickPointBalance) } })
        }

        PreviewCloseButton(
            theme = MainThemes.Theme_A,
            isPreview = preSubTitle == null
        )
    }
}

@Composable
private fun ColumnD(
    preSubTitle: String?,
    isPreview: Boolean,
    sendEvent: (IdleContract.Event) -> Unit,
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 81f.dpx, horizontal = 40f.dpx),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(width = 162f.dpx, height = 36f.dpx),
                painter = painterResource(R.drawable.logo_common02),
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.size(92f.dpx)
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = preSubTitle ?: config.subTitle,
                fontSize = 30f.spx,
                lineHeight = 40.5.spx,
                fontWeight = FontWeight.Normal,
                color = common02,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.size(18f.dpx)
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = config.storeName,
                fontSize = 80f.spx,
                lineHeight = 80f.spx,
                fontWeight = FontWeight.Bold,
                color = common02,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(60.dpx))
            PointBalanceButton(onClick = if (isPreview) {{}} else { { sendEvent(IdleContract.Event.OnClickPointBalance) } })
        }

        PreviewCloseButton(
            theme = MainThemes.Theme_B,
            isPreview = preSubTitle == null,
        )
    }
}

@Composable
private fun ColumnE(
    preSubTitle: String?,
    isPreview: Boolean,
    sendEvent: (IdleContract.Event) -> Unit,
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 80f.dpx, horizontal = 40f.dpx),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(610.dpx))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = config.storeName,
            fontSize = 80f.spx,
            lineHeight = 80f.spx,
            fontWeight = FontWeight.Bold,
            color = common02,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(27.dpx))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = preSubTitle ?: config.subTitle,
            fontSize = 30.spx,
            lineHeight = 40.5.spx,
            fontWeight = FontWeight.Normal,
            fontFamily = NotoSansKr,
            color = common02,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(30.dpx))
        PointBalanceButton(onClick = if (isPreview) {{}} else { { sendEvent(IdleContract.Event.OnClickPointBalance) } })

        Spacer(modifier = Modifier.weight(1f))
        PreviewCloseButton(
            theme = MainThemes.Theme_C,
            isPreview = preSubTitle == null
        )

    }
}

@Composable
private fun ColumnB(
    preSubTitle: String?,
    isPreview: Boolean,
    sendEvent: (IdleContract.Event) -> Unit,
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 80f.dpx, horizontal = 40f.dpx),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 61f.dpx)
        ) {
            Spacer(
                modifier = Modifier.size(86f.dpx)
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = preSubTitle ?: config.subTitle,
                fontSize = 30.spx,
                lineHeight = 40.5.spx,
                fontWeight = FontWeight.Normal,
                color = white,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.size(18.dpx))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = config.storeName,
                fontSize = 80.spx,
                lineHeight = 80.spx,
                fontWeight = FontWeight.Bold,
                color = white,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.size(60.dpx))
            PointBalanceButton(onClick = if (isPreview) {{}} else { { sendEvent(IdleContract.Event.OnClickPointBalance) } })
        }

        PreviewCloseButton(
            theme = MainThemes.Theme_D,
            isPreview = preSubTitle == null
        )
    }
}

@Composable
private fun ColumnA(
    preSubTitle: String?,
    isPreview: Boolean,
    sendEvent: (IdleContract.Event) -> Unit,
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 80f.dpx, horizontal = 40.dpx),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 61.dpx)
        ) {
            Spacer(
                modifier = Modifier.size(86f.dpx)
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = preSubTitle ?: config.subTitle,
                fontSize = 30f.spx,
                lineHeight = 40.5.spx,
                fontWeight = FontWeight.Normal,
                color = white,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.size(18.dpx))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = config.storeName,
                fontSize = 80f.spx,
                lineHeight = 80f.spx,
                fontWeight = FontWeight.Bold,
                color = white,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.size(60.dpx))
            PointBalanceButton(onClick = if (isPreview) {{}} else { { sendEvent(IdleContract.Event.OnClickPointBalance) } })
        }

        PreviewCloseButton(
            theme = MainThemes.Theme_E,
            isPreview = preSubTitle == null
        )
    }
}

@Composable
private fun CustomPreview(
    preSubTitle: String?,
    isPreview: Boolean,
    sendEvent: (IdleContract.Event) -> Unit,
) {
    val controller = LocalController.current
    val config by controller.configState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 80f.dpx, horizontal = 40f.dpx),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .padding(top = 50.dpx)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (isPreview) {
                Box(
                    modifier = Modifier
                        .width(650.dpx)
                        .height(170.dpx)
                        .background(
                            color = Color(0x97000000),
                            shape = RoundedCornerShape(size = 10.dp)
                        )
                        .padding(vertical = 40.dpx, horizontal = 50.dpx)
                ) {
                    Text(
                        text = "사용자 지정 이미지를 사용할 경우\n약국명과 서브타이틀은 표시되지 않습니다.",
                        style = TextStyle(
                            fontSize = 30.spx,
                            lineHeight = 44.8.spx,
                            fontFamily = NotoSansKr,
                            fontWeight = FontWeight(400),
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center,
                        )
                    )

                }

            }

        }
        Spacer(Modifier.weight(1f))
        PointBalanceButton(onClick = if (isPreview) {{}} else { { sendEvent(IdleContract.Event.OnClickPointBalance) } })
        Spacer(Modifier.size(50.dpx))
        PreviewCloseButton(
            theme = MainThemes.Theme_CUSTOM,
            isPreview = preSubTitle == null
        )
    }
}

@Composable
private fun PreviewCloseButton(
    isPreview: Boolean,
    theme: MainThemes,
) {
    val controller = LocalController.current
    val alpha = if (theme == MainThemes.Theme_A) 0.5f else 1f
    val image = when (theme) {
        MainThemes.Theme_A,
        MainThemes.Theme_C -> R.drawable.logo_common02

        else -> R.drawable.logo_common01
    }


    if (isPreview) {
        if (theme != MainThemes.Theme_B && theme != MainThemes.Theme_CUSTOM) {
            Image(
                modifier = Modifier
                    .size(width = 162f.dpx, height = 36f.dpx)
                    .alpha(alpha),
                painter = painterResource(image),
                contentDescription = null
            )
        }
    } else {
        ClickSoundButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(112f.dpx),
            backgroundColor = white,
            shape = RoundedCornerShape(20f.dpx),
            border = BorderStroke(width = 1.dp, color = common01),
            onClick = {
//                controller.dispatch(PadAction.OnClickClosePreview)
            }
        ) {
            Text(
                text = "닫기",
                color = common01,
                fontSize = 35f.spx,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

//
//// 미리보기
//private fun previewController(themeIndex: Int = 0) = object : ViewController {
//    override val mainState = MutableStateFlow(MainState())
//    override val configState = MutableStateFlow(
//        ConfigState(
//            storeName = "연세온누리약국",
//            subTitle = "건강 상담, 언제든지 도와드립니다.",
//            themeIndex = themeIndex
//        )
//    )
//    override val previewState = MutableStateFlow(PreviewState())
//    override val settingState = MutableStateFlow(SettingState())
//    override val pointState = MutableStateFlow(PointState())
//    override val customerState = MutableStateFlow(CustomerState())
//    override val customThemeImageUriState = MutableStateFlow<Uri?>(null)
//    override val appEvents: SharedFlow<AppEvent> = MutableSharedFlow()
//    override fun dispatch(action: PadAction) = Unit
//}
//
//@Preview(name = "테마A", device = "spec:width=800px,height=1319px,dpi=213")
//@Composable
//private fun IdleScreenPreview_ThemeA() {
//    CatposPointTheme {
//        CompositionLocalProvider(LocalController provides previewController(0)) {
//            IdleScreen(
//                state = IdleContract.State(isPreview = false),
//                sendEvent = {}
//            )
//        }
//    }
//}
//
//@Preview(name = "테마B", device = "spec:width=800px,height=1319px,dpi=213")
//@Composable
//private fun IdleScreenPreview_ThemeB() {
//    CatposPointTheme {
//        CompositionLocalProvider(LocalController provides previewController(1)) {
//            IdleScreen(
//                state = IdleContract.State(isPreview = false),
//                sendEvent = {}
//            )
//        }
//    }
//}
//
//@Preview(name = "테마C", device = "spec:width=800px,height=1319px,dpi=213")
//@Composable
//private fun IdleScreenPreview_ThemeC() {
//    CatposPointTheme {
//        CompositionLocalProvider(LocalController provides previewController(2)) {
//            IdleScreen(
//                state = IdleContract.State(isPreview = false),
//                sendEvent = {}
//            )
//        }
//    }
//}
//
//@Preview(name = "테마D", device = "spec:width=800px,height=1319px,dpi=213")
//@Composable
//private fun IdleScreenPreview_ThemeD() {
//    CatposPointTheme {
//        CompositionLocalProvider(LocalController provides previewController(3)) {
//            IdleScreen(
//                state = IdleContract.State(isPreview = false),
//                sendEvent = {}
//            )
//        }
//    }
//}
//
//@Preview(name = "테마E", device = "spec:width=800px,height=1319px,dpi=213")
//@Composable
//private fun IdleScreenPreview_ThemeE() {
//    CatposPointTheme {
//        CompositionLocalProvider(LocalController provides previewController(4)) {
//            IdleScreen(
//                state = IdleContract.State(isPreview = false),
//                sendEvent = {}
//            )
//        }
//    }
//}
//
//@Preview(name = "테마CUSTOM", device = "spec:width=800px,height=1319px,dpi=213")
//@Composable
//private fun IdleScreenPreview_ThemeCUSTOM() {
//    CatposPointTheme {
//        CompositionLocalProvider(LocalController provides previewController(5)) {
//            IdleScreen(
//                state = IdleContract.State(isPreview = false),
//                sendEvent = {}
//            )
//        }
//    }
//}
//
//
//@Preview(
//    name = "사용자 지정 미리보기",
//    device = "spec:width=800px,height=1319px,dpi=213",
//    showBackground = true
//)
//@Composable
//private fun CustomPreviewPreview() {
//    CatposPointTheme {
//        CompositionLocalProvider(LocalController provides previewController()) {
//            CustomPreview(
//                preSubTitle = null,
//                isPreview = true
//            )
//        }
//    }
//}

