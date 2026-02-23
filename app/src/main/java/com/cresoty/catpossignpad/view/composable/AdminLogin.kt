package com.cresoty.catpossignpad.view.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cresoty.catpossignpad.R
import com.cresoty.catpossignpad.model.interfaces.PadAction
import com.cresoty.catpossignpad.px2dp
import com.cresoty.catpossignpad.px2sp
import com.cresoty.catpossignpad.view.composable.common.ClickSoundButton
import com.cresoty.catpossignpad.view.composable.common.DialogGenerator
import com.cresoty.catpossignpad.view.composable.setting.SettingButtons
import com.cresoty.catpossignpad.view.controller.LocalController
import com.cresoty.catpossignpad.view.theme.common02
import com.cresoty.catpossignpad.view.theme.main01
import com.cresoty.catpossignpad.view.theme.notice
import com.cresoty.catpossignpad.view.theme.sub01
import com.cresoty.catpossignpad.view.theme.white

enum class PasswordButtonType(val number: String?, val fontSize: Float) {
    NUMBER1("1", 35f),
    NUMBER2("2", 35f),
    NUMBER3("3", 35f),
    NUMBER4("4", 35f),
    NUMBER5("5", 35f),
    DELETE(null, 0f),
    NUMBER6("6", 35f),
    NUMBER7("7", 35f),
    NUMBER8("8", 35f),
    NUMBER9("9", 35f),
    NUMBER0("0", 35f),
    DELETE_ALL("전체삭제", 20f)
}

@Composable
fun AdminLogin(
    input: String = "",
    isPasswordCorrect: Boolean = true,
    onDismiss: () -> Unit = {},
    onClickClose: () -> Unit = {},
    onClickSave: (String) -> Unit = {},
    onClickNumber: (String) -> Unit = {},
    onClickDelete: () -> Unit = {},
    onClickDeleteAll: () -> Unit = {}
) {
    val shape = RoundedCornerShape(20f.px2dp())

    DialogGenerator(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .size(width = 742f.px2dp(), height = 642f.px2dp())
                .background(color = white, shape = shape)
                .padding(18f.px2dp())
        ) {
            Spacer(modifier = Modifier.size(40f.px2dp()))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "관리자 비밀번호",
                    fontSize = 40f.px2sp(),
                    fontWeight = FontWeight.Medium,
                    color = common02
                )
            }

            Spacer(modifier = Modifier.size(25f.px2dp()))

            PasswordField(input)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(94f.px2dp()),
                contentAlignment = Alignment.Center
            ) {
                if (!isPasswordCorrect) {
                    Text(
                        text = "입력하신 비밀번호가 일치하지 않습니다.",
                        fontSize = 25f.px2sp(),
                        color = notice
                    )
                }
            }

            PasswordButtonField(
                onClickNumber = onClickNumber,
                onClickDelete = onClickDelete,
                onClickDeleteAll = onClickDeleteAll
            )

            SettingButtons(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                isLogin = true,
                onClickClose = onClickClose,
                onClickSave = { onClickSave(input) }
            )
        }
    }
}

// 실제 사용 시 (Controller 연결)
@Composable
fun AdminLoginDialog() {
    val controller = LocalController.current
    val setting by controller.settingState.collectAsStateWithLifecycle()

    AdminLogin(
        input = setting.password,
        isPasswordCorrect = setting.isPasswordCorrect,
        onDismiss = {},
        onClickClose = { controller.dispatch(PadAction.CloseDialog) },
        onClickSave = { controller.dispatch(PadAction.OnClickAdminLogin(it)) },
        onClickNumber = { controller.dispatch(PadAction.OnClickPasswordPad(it)) },
        onClickDelete = { controller.dispatch(PadAction.OnClickDeleteLastPassword) },
        onClickDeleteAll = { controller.dispatch(PadAction.OnClickDeleteAllPassword) }
    )
}

@Composable
fun PasswordButtonField(
    onClickNumber: (String) -> Unit = {},
    onClickDelete: () -> Unit = {},
    onClickDeleteAll: () -> Unit = {}
) {
    val shape = RoundedCornerShape(10f.px2dp())
    val buttons = PasswordButtonType.entries

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4f.px2dp())
            .background(color = sub01, shape = shape)
            .padding(vertical = 5f.px2dp(), horizontal = 5f.px2dp()),
        verticalArrangement = Arrangement.spacedBy(5f.px2dp())
    ) {
        buttons.chunked(6).forEach { rows ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(1f.px2dp())
            ) {
                rows.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {  // 여기서 weight 적용
                        PasswordButton(
                            type = item,
                            shape = shape,
                            onClickNumber = onClickNumber,
                            onClickDelete = onClickDelete,
                            onClickDeleteAll = onClickDeleteAll
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PasswordField(input: String) {
    val list = input.map { it.toString() }.toMutableList()
        .apply {
            if (this.size < 5) {
                val addCount = 5 - this.size
                this.addAll(List(addCount) { "" })
            }
        }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10f.px2dp())) {
            for (i in 0 until 5) {
                Box(
                    modifier = Modifier
                        .size(80f.px2dp())
                        .drawBehind {
                            val stroke = 1.dp.toPx()
                            val y = size.height - stroke / 2
                            drawLine(
                                color = main01,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = stroke
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = list[i],
                        fontSize = 40f.px2sp(),
                        color = common02
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordButton(
    type: PasswordButtonType,
    shape: RoundedCornerShape,
    onClickNumber: (String) -> Unit,
    onClickDelete: () -> Unit,
    onClickDeleteAll: () -> Unit
) {
    val number = type.number ?: ""
    val fontSize = type.fontSize

    ClickSoundButton(
        modifier = Modifier
            .fillMaxWidth()
            .height(60f.px2dp()),
        onClick = {
            when (type) {
                PasswordButtonType.DELETE -> onClickDelete()
                PasswordButtonType.DELETE_ALL -> onClickDeleteAll()
                else -> onClickNumber(number)
            }
        },
        shape = shape,
        backgroundColor = white
    ) {
        when (type) {
            PasswordButtonType.DELETE -> {
                Image(
                    modifier = Modifier.size(width = 38f.px2dp(), height = 26f.px2dp()),
                    painter = painterResource(R.drawable.icon_delete),
                    contentDescription = null
                )
            }

            else -> {
                Text(
                    text = number,
                    fontSize = fontSize.px2sp(),
                    color = common02
                )
            }
        }
    }
}

// Preview
@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
fun SettingPasswordInputPreview() {
    AdminLogin(
        input = "123",
        isPasswordCorrect = true
    )
}