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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cresoty.catpospoint.R
import com.cresoty.catpospoint.model.enums.MainThemes
import com.cresoty.catpospoint.presentation.idle.IdleContract
import com.cresoty.catpospoint.presentation.theme.NotoSansKr
import com.cresoty.catpospoint.presentation.theme.common01
import com.cresoty.catpospoint.presentation.theme.common02
import com.cresoty.catpospoint.presentation.theme.dpx
import com.cresoty.catpospoint.presentation.theme.spx
import com.cresoty.catpospoint.presentation.theme.white
import com.cresoty.catpospoint.ui.component.ClickSoundButton
import com.cresoty.catpospoint.ui.component.PointBalanceButton

@Composable
fun IdleScreen(
    state: IdleContract.State,
    sendEvent: (IdleContract.Event) -> Unit
) {
    val config = state.config

    val themeIndex = if (state.isPreview) state.preTheme ?: 0 else config.themeIndex
    val theme = MainThemes.entries.getOrElse(themeIndex) { MainThemes.Theme_CUSTOM }
    val isCustomTheme = theme == MainThemes.Theme_CUSTOM
    // 커스텀 이미지 URI: 프리뷰 모드면 state에서, 아니면 저장된 config에서 로드
    val customImageUri: Uri? = when {
        state.isPreview -> state.preCustomImageUri
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

    val preSubTitle = if (state.isPreview) state.preSubTitle else null

    val block = mapOf<MainThemes, @Composable () -> Unit>(
        MainThemes.Theme_A to { ColumnA(preSubTitle, state.isPreview, config.storeName, config.subTitle, sendEvent) },
        MainThemes.Theme_B to { ColumnB(preSubTitle, state.isPreview, config.storeName, config.subTitle, sendEvent) },
        MainThemes.Theme_C to { ColumnC(preSubTitle, state.isPreview, config.storeName, config.subTitle, sendEvent) },
        MainThemes.Theme_D to { ColumnD(preSubTitle, state.isPreview, config.storeName, config.subTitle, sendEvent) },
        MainThemes.Theme_E to { ColumnE(preSubTitle, state.isPreview, config.storeName, config.subTitle, sendEvent) },
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
    storeName: String,
    subTitle: String,
    sendEvent: (IdleContract.Event) -> Unit,
) {
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
                text = preSubTitle ?: subTitle,
                fontSize = 30f.spx,
                lineHeight = 40.5.spx,
                fontWeight = FontWeight.Normal,
                color = common02,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(18.dpx))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = storeName,
                fontSize = 80f.spx,
                lineHeight = 80f.spx,
                fontWeight = FontWeight.Bold,
                color = common02,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(60.dpx))
            PointBalanceButton(
                onClick = if (isPreview) {
                    {}
                } else {
                    { sendEvent(IdleContract.Event.OnClickPointBalance) }
                })
        }

        PreviewCloseButton(
            theme = MainThemes.Theme_A,
            isPreview = preSubTitle == null,
            onClickClose = {sendEvent(IdleContract.Event.OnClickClose)}
        )
    }
}

@Composable
private fun ColumnD(
    preSubTitle: String?,
    isPreview: Boolean,
    storeName: String,
    subTitle: String,
    sendEvent: (IdleContract.Event) -> Unit,
) {
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
                text = preSubTitle ?: subTitle,
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
                text = storeName,
                fontSize = 80f.spx,
                lineHeight = 80f.spx,
                fontWeight = FontWeight.Bold,
                color = common02,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(60.dpx))
            PointBalanceButton(
                onClick = if (isPreview) {
                    {}
                } else {
                    { sendEvent(IdleContract.Event.OnClickPointBalance) }
                })
        }

        PreviewCloseButton(
            theme = MainThemes.Theme_B,
            isPreview = preSubTitle == null,
            onClickClose = {sendEvent(IdleContract.Event.OnClickClose)}
        )
    }
}

@Composable
private fun ColumnE(
    preSubTitle: String?,
    isPreview: Boolean,
    storeName: String,
    subTitle: String,
    sendEvent: (IdleContract.Event) -> Unit,
) {
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
            text = storeName,
            fontSize = 80f.spx,
            lineHeight = 80f.spx,
            fontWeight = FontWeight.Bold,
            color = common02,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(27.dpx))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = preSubTitle ?: subTitle,
            fontSize = 30.spx,
            lineHeight = 40.5.spx,
            fontWeight = FontWeight.Normal,
            fontFamily = NotoSansKr,
            color = common02,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(30.dpx))
        PointBalanceButton(
            onClick = if (isPreview) {
                {}
            } else {
                { sendEvent(IdleContract.Event.OnClickPointBalance) }
            })

        Spacer(modifier = Modifier.weight(1f))
        PreviewCloseButton(
            theme = MainThemes.Theme_C,
            isPreview = preSubTitle == null,
            onClickClose = {sendEvent(IdleContract.Event.OnClickClose)}
        )

    }
}

@Composable
private fun ColumnB(
    preSubTitle: String?,
    isPreview: Boolean,
    storeName: String,
    subTitle: String,
    sendEvent: (IdleContract.Event) -> Unit,
) {
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
                text = preSubTitle ?: subTitle,
                fontSize = 30.spx,
                lineHeight = 40.5.spx,
                fontWeight = FontWeight.Normal,
                color = white,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.size(18.dpx))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = storeName,
                fontSize = 80.spx,
                lineHeight = 80.spx,
                fontWeight = FontWeight.Bold,
                color = white,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.size(60.dpx))
            PointBalanceButton(
                onClick = if (isPreview) {
                    {}
                } else {
                    { sendEvent(IdleContract.Event.OnClickPointBalance) }
                })
        }

        PreviewCloseButton(
            theme = MainThemes.Theme_D,
            isPreview = preSubTitle == null,
            onClickClose = {sendEvent(IdleContract.Event.OnClickClose)}
        )
    }
}

@Composable
private fun ColumnA(
    preSubTitle: String?,
    isPreview: Boolean,
    storeName: String,
    subTitle: String,
    sendEvent: (IdleContract.Event) -> Unit,
) {
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
                text = preSubTitle ?: subTitle,
                fontSize = 30f.spx,
                lineHeight = 40.5.spx,
                fontWeight = FontWeight.Normal,
                color = white,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.size(18.dpx))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = storeName,
                fontSize = 80f.spx,
                lineHeight = 80f.spx,
                fontWeight = FontWeight.Bold,
                color = white,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.size(60.dpx))
            PointBalanceButton(
                onClick = if (isPreview) {
                    {}
                } else {
                    { sendEvent(IdleContract.Event.OnClickPointBalance) }
                })
        }

        PreviewCloseButton(
            theme = MainThemes.Theme_E,
            isPreview = preSubTitle == null,
            onClickClose = {sendEvent(IdleContract.Event.OnClickClose)}
        )
    }
}

@Composable
private fun CustomPreview(
    preSubTitle: String?,
    isPreview: Boolean,
    sendEvent: (IdleContract.Event) -> Unit,
) {
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
        PointBalanceButton(
            onClick = if (isPreview) {
                {}
            } else {
                { sendEvent(IdleContract.Event.OnClickPointBalance) }
            })
        Spacer(Modifier.size(50.dpx))
        PreviewCloseButton(
            theme = MainThemes.Theme_CUSTOM,
            isPreview = preSubTitle == null,
            onClickClose = {sendEvent(IdleContract.Event.OnClickClose)}
        )
    }
}

@Composable
private fun PreviewCloseButton(
    isPreview: Boolean,
    theme: MainThemes,
    onClickClose: () -> Unit
) {
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
            onClick = onClickClose
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
